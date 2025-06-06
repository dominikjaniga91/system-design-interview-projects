package url.shortener

import io.restassured.RestAssured
import spark.Spark
import spock.lang.Specification

import static io.restassured.RestAssured.given
import static io.restassured.RestAssured.when
import static org.hamcrest.Matchers.equalTo

class UrlControllerIntegrationSpec extends Specification {

    def setupSpec() {
        Spark.get("/shortener/api/v1/test", (req, res) -> "Test");
        def urlController = new UrlController(new UrlShortenerMock(), new InMemoryUrlRepository());
        urlController.routes();
        Spark.awaitInitialization();
        RestAssured.baseURI = "http://localhost/shortener/api/v1";
        RestAssured.port = 4567;
    }

    def "should return valid short url when sending post request"() {
        expect:
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

    def "should redirect to long url when sending get request"() {
        expect:
        when()
            .get("/xkv3d")
        .then()
            .statusCode(200)
            .body(equalTo("Test"))
    }

    def cleanupSpec() {
        Spark.stop();
    }
}
