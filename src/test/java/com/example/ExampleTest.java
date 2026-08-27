package com.example;

import org.junit.jupiter.api.Test;

import com.microsoft.playwright.*;
import com.microsoft.playwright.options.AriaRole;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class ExampleTest {

    @Test
    void quizAppFlow() {

        try (Playwright playwright = Playwright.create()) {

            Browser browser = playwright.chromium().launch(
                new BrowserType.LaunchOptions()
                    .setHeadless(false)
                    .setSlowMo(500)
            );

            BrowserContext context = browser.newContext();
            Page page = context.newPage();

            // Open Quiz Application
            page.navigate("http://localhost:8111/quizapp/");

            // Verify page loaded
            assertTrue(
                page.title() != null,
                "Quiz application page did not load"
            );

            System.out.println("Quiz application loaded successfully.");

            // Click category
            page.locator("#quizap__Categories__el_btn_3_0").click();

            // Enter Answer 1
            page.locator("#quizap__Questions__el_inp_1_0").click();
            page.locator("#quizap__Questions__el_inp_1_0").fill("A");

            // Enter Answer 2
            page.locator("#quizap__Questions__el_inp_1_1").click();
            page.locator("#quizap__Questions__el_inp_1_1").fill("B");

            // Enter Answer 3
            page.locator("#quizap__Questions__el_inp_1_2").click();
            page.locator("#quizap__Questions__el_inp_1_2").fill("A");

            // Enter Answer 4
            page.locator("#quizap__Questions__el_inp_1_3").click();
            page.locator("#quizap__Questions__el_inp_1_3").fill("B");

            // Enter Answer 5
            page.locator("#quizap__Questions__el_inp_1_4").click();
            page.locator("#quizap__Questions__el_inp_1_4").fill("D");

            // Click Next
            page.locator("#quizap__Questions__el_btn_2_li").click();

            // Click Submit
            page.getByRole(
                AriaRole.BUTTON,
                new Page.GetByRoleOptions().setName("Submit")
            ).click();

            // Click OK on confirmation/alert
            page.getByRole(
                AriaRole.BUTTON,
                new Page.GetByRoleOptions().setName("Ok")
            ).click();

            // Click View Results
            page.getByRole(
                AriaRole.BUTTON,
                new Page.GetByRoleOptions().setName("view results")
            ).click();

            // Keep browser open for 5 seconds so we can see the result
            page.waitForTimeout(5000);

            System.out.println("Quiz test completed successfully.");

            // Close browser
            context.close();
            browser.close();
        }
    }
}