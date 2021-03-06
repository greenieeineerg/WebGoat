package org.owasp.webgoat;

import io.restassured.RestAssured;
import org.assertj.core.api.Assertions;
import org.junit.Test;

import java.util.Map;

public class PasswordResetLessonTest extends IntegrationTest {

    @Test
    public void solveAssignment() {
        //WebGoat
        startLesson("PasswordReset");
        clickForgotEmailLink("tom@webgoat-cloud.org");

        //WebWolf
        var link = getPasswordResetLinkFromLandingPage();

        //WebGoat
        changePassword(link);
        checkAssignment(url("PasswordReset/reset/login"), Map.of("email", "tom@webgoat-cloud.org", "password", "123456"), true);
    }

    @Test
    public void sendEmailShouldBeAvailabeInWebWolf() {
        startLesson("PasswordReset");
        clickForgotEmailLink(getWebgoatUser() + "@webgoat.org");

        var responseBody = RestAssured.given()
                .when()
                .config(restConfig)
                .cookie("WEBWOLFSESSION", getWebWolfCookie())
                .get(webWolfUrl("/WebWolf/mail"))
                .then()
                .extract().response().getBody().asString();

        Assertions.assertThat(responseBody).contains("Hi, you requested a password reset link");
    }

    private void changePassword(String link) {
        RestAssured.given()
                .when()
                .config(restConfig)
                .cookie("JSESSIONID", getWebGoatCookie())
                .formParams("resetLink", link, "password", "123456")
                .post(url("PasswordReset/reset/change-password"))
                .then()
                .statusCode(200);
    }

    private String getPasswordResetLinkFromLandingPage() {
        var responseBody = RestAssured.given()
                .when()
                .config(restConfig)
                .cookie("WEBWOLFSESSION", getWebWolfCookie())
                .get(webWolfUrl("WebWolf/requests"))
                .then()
                .extract().response().getBody().asString();
        int startIndex = responseBody.lastIndexOf("\"path\" : \"/PasswordReset/reset/reset-password/");
        var link = responseBody.substring(startIndex + "\"path\" : \"/PasswordReset/reset/reset-password/".length(), responseBody.indexOf(",", startIndex) - 1);
        return link;
    }

    private void clickForgotEmailLink(String user) {
        RestAssured.given()
                .when()
                .header("host", "localhost:9090")
                .config(restConfig)
                .cookie("JSESSIONID", getWebGoatCookie())
                .formParams("email", user)
                .post(url("PasswordReset/ForgotPassword/create-password-reset-link"))
                .then()
                .statusCode(200);
    }
}
