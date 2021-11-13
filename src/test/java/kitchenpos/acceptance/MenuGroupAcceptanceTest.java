package kitchenpos.acceptance;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.List;
import kitchenpos.domain.MenuGroup;
import kitchenpos.exception.ExceptionResponse;
import kitchenpos.ui.request.MenuGroupRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

@DisplayName("MenuGroup 인수 테스트")
public class MenuGroupAcceptanceTest extends AcceptanceTest {

    @DisplayName("POST /api/menu-groups")
    @Nested
    class Post {

        @DisplayName("정상적인 경우 상태코드 201이 반환된다.")
        @Test
        void createPost() {
            // given
            MenuGroupRequest request = new MenuGroupRequest("킹갓메뉴그룹");

            // when
            ExtractableResponse<Response> response = RestAssured.given()
                .log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(request)
                .when().post("/api/menu-groups")
                .then().log().all()
                .statusCode(CREATED.value())
                .extract();

            // then
            assertThat(response.statusCode()).isEqualTo(CREATED.value());
            assertThat(response.header("Location")).isNotNull();
            assertThat(response.body()).isNotNull();
        }

        @DisplayName("name이 Null인 경우 상태코드 500이 반환된다.")
        @Test
        void nameNull() {
            // given
            MenuGroupRequest request = new MenuGroupRequest(null);

            // when
            ExtractableResponse<Response> response = RestAssured.given()
                .log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(request)
                .when().post("/api/menu-groups")
                .then().log().all()
                .statusCode(BAD_REQUEST.value())
                .extract();

            // then
            assertThat(response.statusCode()).isEqualTo(BAD_REQUEST.value());
            assertThat(response.as(ExceptionResponse.class)).isNotNull();
        }
    }

    @DisplayName("GET /api/menu-groups - 모든 MenuGroup과 상태코드 200이 반환된다.")
    @Test
    void createGet() {
        // when
        ExtractableResponse<Response> response = RestAssured
            .given().log().all()
            .when().get("/api/menu-groups")
            .then().log().all()
            .statusCode(OK.value())
            .extract();

        List<MenuGroup> menuGroups = response.jsonPath().getList(".", MenuGroup.class);

        // then
        assertThat(response.statusCode()).isEqualTo(OK.value());
        assertThat(menuGroups).isNotNull();
    }
}
