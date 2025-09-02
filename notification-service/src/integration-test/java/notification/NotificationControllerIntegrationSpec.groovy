package notification

import io.restassured.RestAssured
import spark.Spark
import spock.lang.Specification

import static io.restassured.RestAssured.given
import static org.hamcrest.Matchers.equalTo

class NotificationControllerIntegrationSpec extends Specification {

    def setupSpec() {
        new NotificationController(new InMemoryNotificationRepository()).routes();
        Spark.awaitInitialization();
        RestAssured.baseURI = "http://localhost/api/v1/notification";
        RestAssured.port = 4567;
    }

    def "should return response when send notification message"() {
        expect:
        given()
                .contentType("application/json")
                .body("""
                    {
                        "serviceName": "payment-service",
                        "types": ["SMS", "EMAIL"],
                        "sender": "435265",
                        "recipientsIds": ["32414", "412341"],
                        "title": "Your account has been verified",
                        "message": "Your account has been verified successfully!" 
                    }
                    """)
        .when()
                .post()
        .then()
                .statusCode(200)
                .body(equalTo("Notification received, id: 1"));
    }
}
