# E-Commerce Stripe Integration (Spring Boot 4)

Dự án mẫu xây dựng hệ thống thương mại điện tử đơn giản (đơn hàng và sản phẩm) kết hợp tích hợp cổng thanh toán trực tuyến **Stripe** một cách bảo mật, sạch sẽ theo kiến trúc chuyên nghiệp.

---

## 📌 Giới thiệu dự án

Dự án được xây dựng bằng Java/Spring Boot để hướng dẫn quy trình tạo đơn hàng và thanh toán qua cổng thanh toán quốc tế Stripe. Hệ thống áp dụng nguyên lý thiết kế **Single Responsibility (Đơn trách nhiệm)** để phân tách hoàn toàn giữa luồng nghiệp vụ Đặt hàng (Order) và luồng thanh toán (Payment).

### Công nghệ sử dụng:
* **Backend:** Spring Boot 4.1.0, Spring Data JPA
* **Database:** MySQL 8.x
* **Mapping & Tooling:** MapStruct, Lombok, Validation API
* **Thanh toán:** Stripe Java SDK

---

## 📂 Quản lý các nhánh Git (Git Branches)

Dự án được tổ chức thành 2 nhánh chính giúp lập trình viên dễ dàng theo dõi từ lúc bắt đầu cho đến khi hoàn thành:

1. **Nhánh `master`:**
   * **Mục tiêu:** Khung dự án cơ bản (CRUD Sản phẩm, Đặt hàng, Dịch vụ).
   * **Nội dung:** Chứa các thiết lập cơ sở dữ liệu MySQL, Entity (`Product`, `Order`, `OrderDetail`), DTOs, Repository và Controller. Chưa cài đặt Stripe để làm bước chuẩn bị ban đầu.
2. **Nhánh `integrate-stripe`:**
   * **Mục tiêu:** Nhánh bắt đầu và hoàn thành tích hợp Stripe.
   * **Nội dung:** Chứa toàn bộ cài đặt về Stripe SDK, cấu hình API, luồng tạo liên kết thanh toán `Payment` và xử lý Stripe Webhook tự động cập nhật trạng thái đơn hàng.

---

## 💻 Hướng dẫn Setup & Chạy dự án

### 1. Yêu cầu hệ thống
* Java Development Kit (JDK) 25.
* Docker và Docker Compose (để khởi chạy MySQL).

### 2. Khởi chạy Cơ sở dữ liệu (MySQL 8)
Dự án đã cấu hình sẵn Docker Compose. Chạy lệnh sau tại thư mục gốc để khởi chạy MySQL:
```bash
docker-compose up -d
```

### 3. Cấu hình ứng dụng
Mở tệp [application.yaml](./src/main/resources/application.yaml) và điền các thông tin môi trường hoặc chạy ứng dụng kèm biến môi trường:
```yaml
stripe:
  api-key: ${STRIPE_API_KEY:sk_test_YOUR_SECRET_KEY}
  success-url: http://localhost:8080/api/payments/success
  cancel-url: http://localhost:8080/api/payments/cancel
```

### 4. Biên dịch và chạy dự án
Chạy ứng dụng Spring Boot bằng lệnh Maven:
```bash
mvn clean spring-boot:run
```

---

## 💳 Stripe là gì và Cách thức hoạt động?

**Stripe** là một trong những cổng thanh toán trực tuyến phổ biến nhất toàn cầu, cho phép các doanh nghiệp nhận thanh toán từ thẻ tín dụng (Visa, Mastercard, JCB), Apple Pay, Google Pay một cách dễ dàng và bảo mật đạt chuẩn PCI.

### Luồng thanh toán Stripe Checkout (Redirect Flow):

![Stripe Checkout Sequence Diagram](https://res.cloudinary.com/drdskl2up/image/upload/v1784366420/a9557fe0-4ba8-46f1-89fb-0baeaa8f8d8e.png)

---

## 🛠️ Các bước triển khai tích hợp Stripe chi tiết

> [!NOTE]
> **Tài liệu tham khảo phát triển:**
> * Tài liệu chính thức: [Stripe API Reference (Java)](https://docs.stripe.com/api?lang=java)
> * API tạo phiên thanh toán: [Create Checkout Session API (Java)](https://docs.stripe.com/api/checkout/sessions/create?lang=java)

Nếu bạn đang ở nhánh `master` và muốn tích hợp Stripe, hãy thực hiện theo các bước sau:

### Bước 1: Lấy API Key từ Stripe Dashboard
1. Truy cập trang **[Đăng ký Stripe](https://dashboard.stripe.com/register)** hoặc **[Đăng nhập Stripe](https://dashboard.stripe.com/login)** để tạo/vào tài khoản miễn phí (khuyên dùng chọn **Đăng nhập/Đăng ký nhanh bằng tài khoản Google** để hoàn thành nhanh nhất).
2. Đi tới trang quản lý **[Stripe API Keys](https://dashboard.stripe.com/acct_1TuRF9JMfqNJ7ArA/test/apikeys)** (hoặc bật chế độ **Developers** ở góc trên bên phải ➜ chọn tab **API keys** ở menu bên trái).
3. Lấy **Secret key** (mã bắt đầu bằng `sk_test_...`) để điền vào cấu hình của dự án.

### Bước 2: Thêm thư viện Stripe vào `pom.xml`
Thêm dependency sau vào thẻ `<dependencies>`:
```xml
<dependency>
    <groupId>com.stripe</groupId>
    <artifactId>stripe-java</artifactId>
    <version>33.1.1</version>
</dependency>
```

### Bước 3: Định nghĩa Thực thể Giao dịch (`Payment`)
Tạo thực thể `Payment` để quản lý lịch sử giao dịch và liên kết với `Order`:
* **Trường dữ liệu quan trọng:** `order`, `paymentGateway` (STRIPE), `amount`, `currency`, `gatewayTransactionId` (Lưu Stripe Session ID để đối soát), và `paymentStatus` (PENDING, SUCCESS, FAILED).

### Bước 4: Tạo cấu hình `StripeClient` Bean
Tạo cấu hình khởi tạo client của Stripe từ `Secret key`:
```java
@Configuration
public class StripeClientConfiguration {
    @Value("${stripe.api-key}")
    private String apiKey;

    @Bean
    public StripeClient stripeClient() {
        return StripeClient.builder().setApiKey(apiKey).build();
    }
}
```

> [!TIP]
> **`StripeClient` là gì và dùng để làm gì?**
> * **Định nghĩa đơn giản:** `StripeClient` là công cụ chính giúp bạn gửi các yêu cầu và kết nối từ server của mình tới hệ thống của Stripe.
> * **Tại sao nên dùng:**
>   * **An toàn khi chạy đa luồng:** Nó giúp tránh các lỗi tranh chấp dữ liệu so với cách cấu hình tĩnh `Stripe.apiKey = ...` kiểu cũ.
>   * **Dễ kết hợp với Spring:** Khai báo nó thành một `@Bean` để dễ dàng tiêm (inject) vào các class khác khi cần dùng.
>   * **Viết code gọn hơn:** Cung cấp sẵn cấu trúc gọi dịch vụ rất trực quan, ví dụ: `stripeClient.v1().checkout().sessions().create(...)`.

### Bước 5: Viết code xử lý thanh toán (Payment Integration)
Chúng ta sẽ chia nhiệm vụ thanh toán ra làm 2 file riêng biệt cho dễ quản lý và nâng cấp:

#### 1. Lớp kết nối trực tiếp với Stripe: `StripePaymentService.java`
File này chỉ có duy nhất nhiệm vụ: Nhận thông tin đơn hàng từ hệ thống ➜ chuẩn hóa số tiền ➜ gọi API sang Stripe để tạo phiên thanh toán và trả về kết quả.

```java
@Service
@RequiredArgsConstructor
@Slf4j(topic = "STRIPE-SERVICE")
public class StripePaymentService {

    // 1. Lấy link redirect khi thanh toán xong từ application.yaml
    @Value("${stripe.success-url}")
    private String successUrl;

    @Value("${stripe.cancel-url}")
    private String cancelUrl;

    private final StripeClient stripeClient;
    private final OrderRepository orderRepository;

    @Transactional
    public Session createCheckoutSession(CreateLinkPaymentRequest request) {
        // 2. Tìm đơn hàng cần thanh toán
        Order order = orderRepository.findByOrderCode(request.orderCode())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng: " + request.orderCode()));

        // 3. Thiết lập các thông tin cơ bản gửi lên Stripe
        SessionCreateParams.Builder sessionBuilder = SessionCreateParams.builder()
                .setSuccessUrl(successUrl + "?session_id={CHECKOUT_SESSION_ID}") // Stripe sẽ tự động điền mã session vào URL này khi thành công
                .setCancelUrl(cancelUrl)
                .setClientReferenceId(order.getOrderCode()) // Gắn mã đơn hàng của mình vào đây để tiện đối soát
                .putMetadata("orderCode", order.getOrderCode())
                .setMode(SessionCreateParams.Mode.PAYMENT);

        // 4. Duyệt qua từng sản phẩm trong đơn hàng để hiển thị đầy đủ trên trang Stripe Checkout
        for (OrderDetail detail : order.getOrderDetails()) {
            Product product = detail.getProduct();
            // Đổi số tiền sang định dạng Stripe yêu cầu (VND giữ nguyên, USD nhân với 100)
            Long itemAmount = toStripeAmount(detail.getPrice(), order.getCurrency());

            SessionCreateParams.LineItem.PriceData.ProductData.Builder productDataBuilder =
                    SessionCreateParams.LineItem.PriceData.ProductData.builder()
                            .setName(product.getName());

            // Đưa link ảnh sản phẩm lên trang Stripe Checkout nếu có
            if (product.getImageUrl() != null && !product.getImageUrl().isBlank()) {
                productDataBuilder.addImage(product.getImageUrl());
            }

            sessionBuilder.addLineItem(SessionCreateParams.LineItem.builder()
                    .setQuantity(detail.getQuantity().longValue())
                    .setPriceData(SessionCreateParams.LineItem.PriceData.builder()
                            .setCurrency(order.getCurrency().toStripeCode())
                            .setUnitAmount(itemAmount)
                            .setProductData(productDataBuilder.build())
                            .setTaxBehavior(SessionCreateParams.LineItem.PriceData.TaxBehavior.INCLUSIVE)
                            .build())
                    .build());
        }

        SessionCreateParams params = sessionBuilder.build();

        // 5. Dùng Idempotency Key để đảm bảo không bị trừ tiền trùng lặp nếu gửi request 2 lần
        RequestOptions options = RequestOptions.builder()
                .setIdempotencyKey("checkout-" + order.getOrderCode())
                .build();

        try {
            // Gọi Stripe SDK để tạo link thanh toán
            return stripeClient.v1().checkout().sessions().create(params, options);
        } catch (StripeException e) {
            log.error("Tạo phiên thanh toán trên Stripe thất bại", e);
            throw new RuntimeException("Tích hợp Stripe gặp lỗi", e);
        }
    }

    // Tiền VND không chứa phần thập phân (Zero-decimal), còn USD phải nhân 100 để đổi sang cents
    private Long toStripeAmount(BigDecimal amount, Currency currency) {
        return currency.isZeroDecimal() ?
                amount.longValueExact() :
                amount.multiply(BigDecimal.valueOf(100)).longValueExact();
    }
}
```

#### 2. Lớp xử lý Database của hệ thống mình: `PaymentService.java`
File này đóng vai trò trung gian: Lấy thông tin đơn hàng ➜ Nhờ `StripePaymentService` lấy link thanh toán từ Stripe ➜ Lưu lịch sử giao dịch ở trạng thái `PENDING` vào database của mình ➜ Trả kết quả về cho Controller.

```java
@Service
@RequiredArgsConstructor
@Slf4j(topic = "PAYMENT-SERVICE")
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    private final StripePaymentService stripePaymentService;

    @Transactional(rollbackFor = Exception.class)
    public CheckoutResponse checkout(CheckoutRequest request) {
        log.info("Bắt đầu xử lý thanh toán đơn hàng: {}", request.orderCode());

        // 1. Tìm đơn hàng tương ứng
        Order order = orderRepository.findByOrderCode(request.orderCode())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng: " + request.orderCode()));

        // 2. Gọi Stripe lấy thông tin phiên thanh toán
        CreateLinkPaymentRequest stripeRequest = new CreateLinkPaymentRequest(order.getOrderCode());
        Session session = stripePaymentService.createCheckoutSession(stripeRequest);

        // 3. Lưu thông tin giao dịch vào Database của mình ở trạng thái PENDING (đang chờ)
        Payment payment = Payment.builder()
                .order(order)
                .paymentGateway(request.paymentGateway())
                .amount(order.getTotalPrice())
                .currency(order.getCurrency())
                .gatewayTransactionId(session.getId()) // Lưu lại ID session của Stripe để webhook đối chiếu sau này
                .paymentStatus(PaymentStatus.PENDING)
                .build();
        paymentRepository.save(payment);

        log.info("Lưu giao dịch thành công. ID: {}, Stripe Session ID: {}", payment.getId(), session.getId());

        // 4. Trả kết quả link thanh toán về cho Controller để phản hồi cho client
        return CheckoutResponse.builder()
                .paymentUrl(session.getUrl())
                .gatewayTransactionId(session.getId())
                .build();
    }
}
```

### Bước 6: Tự động cập nhật đơn hàng qua Webhook
1. Khi khách hàng trả tiền xong, Stripe sẽ tự động gửi một thông báo (sự kiện `checkout.session.completed`) về đường dẫn Webhook của bạn.
2. Hệ thống của bạn nhận thông báo này, lấy `Session ID` của Stripe ra để tra cứu giao dịch `Payment` tương ứng trong Database.
3. Chuyển trạng thái giao dịch `Payment` và đơn hàng `Order` sang `SUCCESS` (Đã thanh toán xong).

---

## 🧪 Hướng dẫn Kiểm thử (Testing)

Bạn có thể chạy kiểm thử toàn bộ luồng thông qua tệp tin **[api-requests.http](./api-requests.http)**:

1. **Tạo sản phẩm:** Nhấn chạy lần lượt các request tạo từ Sản phẩm 1 đến Sản phẩm 5 để thêm dữ liệu mẫu vào DB.
2. **Tạo đơn hàng:** Nhấn chạy **Create Order** để tạo đơn hàng. Lưu lại mã đơn hàng `"orderCode"` (Ví dụ: `ORD-123456`) và `"id"` đơn hàng trả về.
3. **Thanh toán:**
   * Sử dụng request checkout trong tệp **[api-requests.http](./api-requests.http)**.
   * Thay thế `"orderCode"` bằng mã đơn hàng bạn vừa copy ở bước 2.
   * Gửi request `POST /api/payments/checkout`. Nhận về đường dẫn thanh toán `paymentUrl`.
4. **Giả lập thanh toán trên giao diện Stripe:**
   * Click vào đường dẫn `paymentUrl` để chuyển đến trang thanh toán của Stripe.
   * Sử dụng thông tin thẻ test dưới đây để điền:
     * **Số thẻ (Card number):** `4242 4242 4242 4242`
     * **Hạn dùng (MM/YY):** Bất kỳ tháng/năm nào trong tương lai (Ví dụ: `12 / 29`)
     * **CVC:** Ba chữ số bất kỳ (Ví dụ: `123`)
     * **Tên chủ thẻ (Cardholder name):** Tên bất kỳ (Ví dụ: `NGUYEN VAN A`)
   * Nhấn **Pay** để xác nhận thanh toán thử nghiệm thành công.
