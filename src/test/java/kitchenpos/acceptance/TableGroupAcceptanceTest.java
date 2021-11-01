package kitchenpos.acceptance;

import static kitchenpos.domain.OrderStatus.COOKING;
import static kitchenpos.domain.OrderStatus.MEAL;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NO_CONTENT;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuProduct;
import kitchenpos.domain.Order;
import kitchenpos.domain.OrderLineItem;
import kitchenpos.domain.OrderStatus;
import kitchenpos.domain.OrderTable;
import kitchenpos.domain.Product;
import kitchenpos.domain.TableGroup;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

@DisplayName("TableGroup 인수 테스트")
public class TableGroupAcceptanceTest extends AcceptanceTest {

    @DisplayName("POST /api/table-groups")
    @Nested
    class Post {

        @DisplayName("TableGroup의 OrderTable의 개수가 2개 미만일 경우 예외가 발생한다.")
        @Test
        void orderTablesUnderTwoException() {
            // given
            TableGroup tableGroup = TableGroup을_생성한다(new OrderTable());

            // when
            ExtractableResponse<Response> response = RestAssured.given()
                .log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(tableGroup)
                .when().post("/api/table-groups")
                .then().log().all()
                .statusCode(INTERNAL_SERVER_ERROR.value())
                .extract();

            // then
            assertThat(response.statusCode()).isEqualTo(INTERNAL_SERVER_ERROR.value());
        }

        @DisplayName("TableGroup의 OrderTable들과 실제 저장된 OrderTable들이 일치하지 않을 경우 예외가 발생한다.")
        @Test
        void nonMatchOrderTablesException() {
            // given
            OrderTable orderTable1 = HTTP_요청을_통해_OrderTable을_생성한다(1);
            OrderTable orderTable2 = HTTP_요청을_통해_OrderTable을_생성한다(2);
            OrderTable orderTable3 = HTTP_요청을_통해_OrderTable을_생성한다(3);
            orderTable3.setId(-1L);

            TableGroup tableGroup = TableGroup을_생성한다(orderTable1, orderTable2, orderTable3);

            // when
            ExtractableResponse<Response> response = RestAssured.given()
                .log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(tableGroup)
                .when().post("/api/table-groups")
                .then().log().all()
                .statusCode(INTERNAL_SERVER_ERROR.value())
                .extract();

            // then
            assertThat(response.statusCode()).isEqualTo(INTERNAL_SERVER_ERROR.value());
        }

        @DisplayName("TableGroup의 OrderTable이 비어있는 상태가 아닐 경우 예외가 발생한다.")
        @Test
        void orderTableIsNotEmptyException() {
            // given
            OrderTable orderTable1 = HTTP_요청을_통해_OrderTable을_생성한다(1, false);
            OrderTable orderTable2 = HTTP_요청을_통해_OrderTable을_생성한다(2, false);
            OrderTable orderTable3 = HTTP_요청을_통해_OrderTable을_생성한다(3, false);

            TableGroup tableGroup = TableGroup을_생성한다(orderTable1, orderTable2, orderTable3);

            // when
            ExtractableResponse<Response> response = RestAssured.given()
                .log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(tableGroup)
                .when().post("/api/table-groups")
                .then().log().all()
                .statusCode(INTERNAL_SERVER_ERROR.value())
                .extract();

            // then
            assertThat(response.statusCode()).isEqualTo(INTERNAL_SERVER_ERROR.value());
        }

        @DisplayName("TableGroup의 OrderTable에 이미 TableGroupId가 등록되어 있는 경우 예외가 발생한다.")
        @Test
        void alreadyMappingTableGroupException() {
            // given
            OrderTable orderTable1 = HTTP_요청을_통해_OrderTable을_생성한다(1, true);
            OrderTable orderTable2 = HTTP_요청을_통해_OrderTable을_생성한다(2, true);
            OrderTable orderTable3 = HTTP_요청을_통해_OrderTable을_생성한다(3, true);

            HTTP_요청을_통해_TableGroup을_생성한다(orderTable1, orderTable2);

            TableGroup tableGroup = TableGroup을_생성한다(orderTable1, orderTable3);

            // when
            ExtractableResponse<Response> response = RestAssured.given()
                .log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(tableGroup)
                .when().post("/api/table-groups")
                .then().log().all()
                .statusCode(INTERNAL_SERVER_ERROR.value())
                .extract();

            // then
            assertThat(response.statusCode()).isEqualTo(INTERNAL_SERVER_ERROR.value());
        }

        @DisplayName("TableGroup의 OrderTable이 정상적인 경우 TableGroup과 연결된다.")
        @Test
        void success() {
            // given
            OrderTable orderTable1 = HTTP_요청을_통해_OrderTable을_생성한다(1, true);
            OrderTable orderTable2 = HTTP_요청을_통해_OrderTable을_생성한다(2, true);
            OrderTable orderTable3 = HTTP_요청을_통해_OrderTable을_생성한다(3, true);

            TableGroup tableGroup = TableGroup을_생성한다(orderTable1, orderTable2, orderTable3);

            // when
            ExtractableResponse<Response> response = RestAssured.given()
                .log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(tableGroup)
                .when().post("/api/table-groups")
                .then().log().all()
                .statusCode(CREATED.value())
                .extract();

            // then
            assertThat(response.statusCode()).isEqualTo(CREATED.value());
            assertThat(response.header("Location")).isNotNull();
            assertThat(response.body()).isNotNull();
        }
    }

    @DisplayName("DELETE /api/table-groups/{tableGroupId}")
    @Nested
    class Ungroup {

        @DisplayName("COOKING 중인 OrderTable이 포함된 Order가 존재하는 경우 예외가 발생한다.")
        @Test
        void existsByOrderTableIdInAndCOOKINGStatusInException() {
            // given
            MenuGroup menuGroup = HTTP_요청을_통해_MenuGroup을_생성한다("엄청난 그룹");

            Product 치즈버거 = HTTP_요청을_통해_Product를_생성한다("치즈버거", 4_000);
            Product 콜라 = HTTP_요청을_통해_Product를_생성한다("치즈버거", 1_600);

            MenuProduct 치즈버거_MenuProduct = MenuProduct를_생성한다(치즈버거.getId(), 1);
            MenuProduct 콜라_MenuProduct = MenuProduct를_생성한다(콜라.getId(), 1);
            List<MenuProduct> menuProducts = Arrays.asList(치즈버거_MenuProduct, 콜라_MenuProduct);

            Menu menu1 = HTTP_요청을_통해_Menu를_생성한다("엄청난 메뉴", 5_600, menuGroup.getId(), menuProducts);
            Menu menu2 = HTTP_요청을_통해_Menu를_생성한다("색다른 메뉴", 4_600, menuGroup.getId(), menuProducts);

            OrderTable orderTable1 = HTTP_요청을_통해_OrderTable을_생성한다(1, true);
            OrderTable orderTable2 = HTTP_요청을_통해_OrderTable을_생성한다(2, true);
            TableGroup tableGroup = HTTP_요청을_통해_TableGroup을_생성한다(orderTable1, orderTable2);

            OrderLineItem orderLineItem1 = OrderLineItem을_생성한다(menu1.getId(), 1);
            OrderLineItem orderLineItem2 = OrderLineItem을_생성한다(menu2.getId(), 1);
            HTTP_요청을_통해_Order를_생성한다(orderTable1.getId(), COOKING, Arrays.asList(orderLineItem1, orderLineItem2));

            // when
            ExtractableResponse<Response> response = RestAssured.given()
                .log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(tableGroup)
                .when().delete(String.format("/api/table-groups/%d", tableGroup.getId()))
                .then().log().all()
                .statusCode(INTERNAL_SERVER_ERROR.value())
                .extract();

            // then
            assertThat(response.statusCode()).isEqualTo(INTERNAL_SERVER_ERROR.value());
        }

        @DisplayName("MEAL 중인 OrderTable이 포함된 Order가 존재하는 경우 예외가 발생한다.")
        @Test
        void existsByOrderTableIdInAndMEALStatusInException() {
            // given
            MenuGroup menuGroup = HTTP_요청을_통해_MenuGroup을_생성한다("엄청난 그룹");

            Product 치즈버거 = HTTP_요청을_통해_Product를_생성한다("치즈버거", 4_000);
            Product 콜라 = HTTP_요청을_통해_Product를_생성한다("치즈버거", 1_600);

            MenuProduct 치즈버거_MenuProduct = MenuProduct를_생성한다(치즈버거.getId(), 1);
            MenuProduct 콜라_MenuProduct = MenuProduct를_생성한다(콜라.getId(), 1);
            List<MenuProduct> menuProducts = Arrays.asList(치즈버거_MenuProduct, 콜라_MenuProduct);

            Menu menu1 = HTTP_요청을_통해_Menu를_생성한다("엄청난 메뉴", 5_600, menuGroup.getId(), menuProducts);
            Menu menu2 = HTTP_요청을_통해_Menu를_생성한다("색다른 메뉴", 4_600, menuGroup.getId(), menuProducts);

            OrderTable orderTable1 = HTTP_요청을_통해_OrderTable을_생성한다(1, true);
            OrderTable orderTable2 = HTTP_요청을_통해_OrderTable을_생성한다(2, true);
            TableGroup tableGroup = HTTP_요청을_통해_TableGroup을_생성한다(orderTable1, orderTable2);

            OrderLineItem orderLineItem1 = OrderLineItem을_생성한다(menu1.getId(), 1);
            OrderLineItem orderLineItem2 = OrderLineItem을_생성한다(menu2.getId(), 1);
            HTTP_요청을_통해_Order를_생성한다(orderTable1.getId(), MEAL, Arrays.asList(orderLineItem1, orderLineItem2));

            // when
            ExtractableResponse<Response> response = RestAssured.given()
                .log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(tableGroup)
                .when().delete(String.format("/api/table-groups/%d", tableGroup.getId()))
                .then().log().all()
                .statusCode(INTERNAL_SERVER_ERROR.value())
                .extract();

            // then
            assertThat(response.statusCode()).isEqualTo(INTERNAL_SERVER_ERROR.value());
        }

        @DisplayName("COOKING, MEAL 중인 OrderTable이 포함된 Order가 존재하지 않는 경우 그룹 해제가 된다.")
        @Test
        void success() {
            // given
            OrderTable orderTable1 = HTTP_요청을_통해_OrderTable을_생성한다(1, true);
            OrderTable orderTable2 = HTTP_요청을_통해_OrderTable을_생성한다(2, true);
            TableGroup tableGroup = HTTP_요청을_통해_TableGroup을_생성한다(orderTable1, orderTable2);

            // when
            ExtractableResponse<Response> response = RestAssured.given()
                .log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(tableGroup)
                .when().delete(String.format("/api/table-groups/%d", tableGroup.getId()))
                .then().log().all()
                .statusCode(NO_CONTENT.value())
                .extract();

            // then
            assertThat(response.statusCode()).isEqualTo(NO_CONTENT.value());
        }
    }

    private TableGroup TableGroup을_생성한다(OrderTable... orderTables) {
        TableGroup tableGroup = new TableGroup();
        tableGroup.setCreatedDate(LocalDateTime.now());
        tableGroup.setOrderTables(Arrays.asList(orderTables));

        return tableGroup;
    }

    private TableGroup HTTP_요청을_통해_TableGroup을_생성한다(OrderTable... orderTables) {
        TableGroup tableGroup = new TableGroup();
        tableGroup.setCreatedDate(LocalDateTime.now());
        tableGroup.setOrderTables(Arrays.asList(orderTables));

        return postRequestWithBody("/api/table-groups", tableGroup).as(TableGroup.class);
    }

    private OrderTable HTTP_요청을_통해_OrderTable을_생성한다(int numberOfGuests) {
        return HTTP_요청을_통해_OrderTable을_생성한다(numberOfGuests, null, false);
    }

    private OrderTable HTTP_요청을_통해_OrderTable을_생성한다(int numberOfGuests, boolean isEmpty) {
        return HTTP_요청을_통해_OrderTable을_생성한다(numberOfGuests, null, isEmpty);
    }

    private OrderTable HTTP_요청을_통해_OrderTable을_생성한다(int numberOfGuests, Long tableGroupId, boolean isEmpty) {
        OrderTable orderTable = new OrderTable();
        orderTable.setNumberOfGuests(numberOfGuests);
        orderTable.setTableGroupId(tableGroupId);
        orderTable.setEmpty(isEmpty);

        return postRequestWithBody("/api/tables", orderTable).as(OrderTable.class);
    }

    private Order HTTP_요청을_통해_Order를_생성한다(Long orderTableId, OrderStatus orderStatus, List<OrderLineItem> orderLineItems) {
        Order order = new Order();
        order.setOrderTableId(orderTableId);
        order.setOrderStatus(orderStatus.name());
        order.setOrderedTime(LocalDateTime.now());
        order.setOrderLineItems(orderLineItems);

        return postRequestWithBody("/api/orders", order).as(Order.class);
    }

    private OrderLineItem OrderLineItem을_생성한다(Long menuId, long quantity) {
        OrderLineItem orderLineItem = new OrderLineItem();
        orderLineItem.setMenuId(menuId);
        orderLineItem.setQuantity(quantity);

        return orderLineItem;
    }

    private Menu HTTP_요청을_통해_Menu를_생성한다(String name, int price, Long menuGroupId, List<MenuProduct> menuProducts) {
        Menu menu = new Menu();
        menu.setName(name);
        menu.setPrice(BigDecimal.valueOf(price));
        menu.setMenuGroupId(menuGroupId);
        menu.setMenuProducts(menuProducts);

        return postRequestWithBody("/api/menus", menu).as(Menu.class);
    }

    private MenuGroup HTTP_요청을_통해_MenuGroup을_생성한다(String name) {
        MenuGroup menuGroup = new MenuGroup();
        menuGroup.setName(name);

        return postRequestWithBody("/api/menu-groups", menuGroup).as(MenuGroup.class);
    }

    private MenuProduct MenuProduct를_생성한다(Long productId, long quantity) {
        MenuProduct menuProduct = new MenuProduct();
        menuProduct.setProductId(productId);
        menuProduct.setQuantity(quantity);

        return menuProduct;
    }

    private Product HTTP_요청을_통해_Product를_생성한다(String name, int price) {
        Product product = new Product();
        product.setName(name);
        product.setPrice(BigDecimal.valueOf(price));

        return postRequestWithBody("/api/products", product).as(Product.class);
    }
}
