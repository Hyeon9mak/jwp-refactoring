package kitchenpos.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;
import java.util.List;
import kitchenpos.dao.ProductDao;
import kitchenpos.domain.Product;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;

@DisplayName("Product Service 테스트")
@SpringBootTest
class ProductServiceTest {

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductDao productDao;

    @DisplayName("Product를 저장할 때")
    @Nested
    class Save {

        @DisplayName("정상적인 Product는 저장에 성공한다.")
        @Test
        void success() {
            // given
            Product product = Product를_생성한다("치즈버거", 4_500);

            // when
            Product savedProduct = productService.create(product);

            // then
            assertThat(productDao.findById(savedProduct.getId())).isPresent();
            assertThat(savedProduct).usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(savedProduct);
        }

        @DisplayName("name이 Null인 경우 예외가 발생한다.")
        @Test
        void nameNullException() {
            // given
            Product menuGroup = Product를_생성한다(null, 4_500);

            // when, then
            assertThatThrownBy(() -> productService.create(menuGroup))
                .isExactlyInstanceOf(DataIntegrityViolationException.class);
        }

        @DisplayName("price가 Null인 경우 예외가 발생한다.")
        @Test
        void priceNullException() {
            // given
            Product menuGroup = Product를_생성한다("치킨버거", null);

            // when, then
            assertThatThrownBy(() -> productService.create(menuGroup))
                .isExactlyInstanceOf(IllegalArgumentException.class);
        }

        @DisplayName("price가 음수인 경우 예외가 발생한다.")
        @Test
        void priceNegativeException() {
            // given
            Product menuGroup = Product를_생성한다("치킨버거", -1);

            // when, then
            assertThatThrownBy(() -> productService.create(menuGroup))
                .isExactlyInstanceOf(IllegalArgumentException.class);
        }
    }

    @DisplayName("모든 Product를 조회한다.")
    @Test
    void findAll() {
        // given
        List<Product> beforeSavedProducts = productService.list();

        beforeSavedProducts.add(productDao.save(Product를_생성한다("치즈버거", 4_500)));
        beforeSavedProducts.add(productDao.save(Product를_생성한다("치킨버거", 5_000)));
        beforeSavedProducts.add(productDao.save(Product를_생성한다("주는대로 먹어", 100_000)));

        // when
        List<Product> afterSavedProducts = productService.list();

        // then
        assertThat(afterSavedProducts).hasSize(beforeSavedProducts.size());
        assertThat(afterSavedProducts).usingRecursiveComparison()
            .isEqualTo(beforeSavedProducts);
    }

    private Product Product를_생성한다(String name, int price) {
        return Product를_생성한다(name, BigDecimal.valueOf(price));
    }

    private Product Product를_생성한다(String name, BigDecimal price) {
        Product product = new Product();
        product.setName(name);
        product.setPrice(price);

        return product;
    }
}