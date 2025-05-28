package url.shortener;

import io.restassured.RestAssured;
import org.junit.jupiter.api.*;
import spark.Spark;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.when;
import static org.hamcrest.Matchers.*;

class UrlControllerIntegrationTest {

    @BeforeAll
    static void setUp() {
        Spark.get("/shortener/api/v1/test" ,(req, res) -> "Test");
        UrlController urlController = new UrlController(new UrlShortenerMock(), new InMemoryUrlRepository());
        urlController.routes();
        Spark.awaitInitialization();
        RestAssured.baseURI = "http://localhost/shortener/api/v1";
        RestAssured.port = 4567;
    }

    @Test
    void shouldReturnValidShortUrl_whenSendingPostRequest() {
        given()
                .contentType("application/json")
                .body("""
                        {
                            "urlValue": "longUrl"
                        }
                        """)
        .when()
                .post()
        .then()
                .statusCode(200)
                .body(equalTo("http://localhost:4567/shortener/api/v1/xkv3d5"));
    }

    @Test
    void shouldRedirectToLongUrl_whenSendingGetRequest() {
        when()
                .get("/xkv3d")
        .then()
                .statusCode(200)
                .body(equalTo("Test"));
    }

    @AfterAll
    static void tearDown() {
        Spark.stop();
    }
}
