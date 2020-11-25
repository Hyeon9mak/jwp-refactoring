package kitchenpos.domain;

import kitchenpos.domain.menu.Menu;
import kitchenpos.domain.product.Price;
import kitchenpos.fixture.MenuFixture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.math.BigDecimal;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

class MenuTest {

    @DisplayName("isSmallerPrice 기능 테스트")
    @ParameterizedTest
    @CsvSource(value = {"2999:true", "3000:false", "3001:false"}, delimiter = ':')
    void isSmallerPrice(int price, boolean expect) {
        Menu menu = MenuFixture.createMenuWithPrice(BigDecimal.valueOf(3000L));

        boolean actual = menu.isSmallerPrice(new Price(price));

        assertThat(actual).isEqualTo(expect);
    }

    @DisplayName("equalsById 기능 테스트")
    @ParameterizedTest
    @CsvSource(value = {"1:true", "2:false"}, delimiter = ':')
    void equalsById(Long id, boolean expect) {
        Menu menu = MenuFixture.createMenuWithId(1L);

        boolean actual = menu.equalsById(id);

        assertThat(actual).isEqualTo(expect);
    }
}