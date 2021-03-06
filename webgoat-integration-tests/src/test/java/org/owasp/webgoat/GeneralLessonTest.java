package org.owasp.webgoat;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;


public class GeneralLessonTest extends IntegrationTest {

    @Test
    public void httpBasics() {
        startLesson("HttpBasics");
        Map<String, Object> params = new HashMap<>();
        params.clear();
        params.put("person", "goatuser");
        checkAssignment(url("HttpBasics/attack1"), params, true);

        params.clear();
        params.put("answer", "POST");
        params.put("magic_answer", "33");
        params.put("magic_num", "4");
        checkAssignment(url("HttpBasics/attack2"), params, false);

        params.clear();
        params.put("answer", "POST");
        params.put("magic_answer", "33");
        params.put("magic_num", "33");
        checkAssignment(url("HttpBasics/attack2"), params, true);

        checkResults("/HttpBasics/");
    }

    @Test
    public void httpProxies() {
        startLesson("HttpProxies");
        Assert.assertThat(RestAssured.given()
                .when().config(restConfig).cookie("JSESSIONID", getWebGoatCookie()).header("x-request-intercepted", "true")
                .contentType(ContentType.JSON)
                .get(url("HttpProxies/intercept-request?changeMe=Requests are tampered easily"))
                .then()
                .statusCode(200).extract().path("lessonCompleted"), CoreMatchers.is(true));

        checkResults("/HttpProxies/");
    }

    @Test
    public void cia() {
        startLesson("CIA");
        Map<String, Object> params = new HashMap<>();
        params.clear();
        params.put("question_0_solution", "Solution 3: By stealing a database where names and emails are stored and uploading it to a website.");
        params.put("question_1_solution", "Solution 1: By changing the names and emails of one or more users stored in a database.");
        params.put("question_2_solution", "Solution 4: By launching a denial of service attack on the servers.");
        params.put("question_3_solution", "Solution 2: The systems security is compromised even if only one goal is harmed.");
        checkAssignment(url("/WebGoat/cia/quiz"), params, true);
        checkResults("/cia/");

    }

    @Test
    public void securePasswords() {
        startLesson("SecurePasswords");
        Map<String, Object> params = new HashMap<>();
        params.clear();
        params.put("password", "ajnaeliclm^&&@kjn.");
        checkAssignment(url("/WebGoat/SecurePasswords/assignment"), params, true);
        checkResults("SecurePasswords/");

        startLesson("AuthBypass");
        params.clear();
        params.put("secQuestion2", "John");
        params.put("secQuestion3", "Main");
        params.put("jsEnabled", "1");
        params.put("verifyMethod", "SEC_QUESTIONS");
        params.put("userId", "12309746");
        checkAssignment(url("/WebGoat/auth-bypass/verify-account"), params, true);
        checkResults("/auth-bypass/");

        startLesson("HttpProxies");
        Assert.assertThat(RestAssured.given().when().config(restConfig).cookie("JSESSIONID", getWebGoatCookie()).header("x-request-intercepted", "true")
                .contentType(ContentType.JSON)
                .get(url("/WebGoat/HttpProxies/intercept-request?changeMe=Requests are tampered easily")).then()
                .statusCode(200).extract().path("lessonCompleted"), CoreMatchers.is(true));
        checkResults("/HttpProxies/");

    }

    @Test
    public void chrome() {
        startLesson("ChromeDevTools");

        Map<String, Object> params = new HashMap<>();
        params.clear();
        params.put("param1", "42");
        params.put("param2", "24");

        String result =
                RestAssured.given()
                        .when()
                        .config(restConfig)
                        .cookie("JSESSIONID", getWebGoatCookie())
                        .header("webgoat-requested-by", "dom-xss-vuln")
                        .header("X-Requested-With", "XMLHttpRequest")
                        .formParams(params)
                        .post(url("/WebGoat/CrossSiteScripting/phone-home-xss"))
                        .then()
                        .statusCode(200)
                        .extract().path("output");
        String secretNumber = result.substring("phoneHome Response is ".length());

        params.clear();
        params.put("successMessage", secretNumber);
        checkAssignment(url("/WebGoat/ChromeDevTools/dummy"), params, true);

        params.clear();
        params.put("number", "24");
        params.put("network_num", "24");
        checkAssignment(url("/WebGoat/ChromeDevTools/network"), params, true);

        checkResults("/ChromeDevTools/");
    }
}
