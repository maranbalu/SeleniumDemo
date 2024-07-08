package org.example.seleniumdemo;

import org.openqa.selenium.bidi.browsingcontext.BrowsingContext;
import org.openqa.selenium.devtools.DevTools;
import org.openqa.selenium.devtools.v124.fetch.model.AuthRequired;
import org.openqa.selenium.devtools.v126.network.Network;
import org.testng.annotations.*;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.time.Duration;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;

public class MainPageTest {
    private WebDriver driver;
    private DevTools devTools;
    private Set<CompletableFuture<Void>> outstandingRequests;

    private static void accept(CompletableFuture<Void> request) {
        try {
            System.out.println(request.get());
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    @BeforeMethod
    public void setUp() {
        ChromeOptions options = new ChromeOptions();
        // Fix the issue https://github.com/SeleniumHQ/selenium/issues/11750
        options.addArguments("--remote-allow-origins=*");
        options.setCapability("webSocketUrl", true);
        driver = new ChromeDriver(options);
        BrowsingContext browsingContext = new BrowsingContext(driver, driver.getWindowHandle());
        System.out.println("Browsing context: " + browsingContext.getId());
        browsingContext.navigate("https://www.jetbrains.com/");
        driver.switchTo().window(browsingContext.getId());
        driver.manage().window().maximize();

        devTools = ((ChromeDriver) driver).getDevTools();
        devTools.createSession();
        outstandingRequests = ConcurrentHashMap.newKeySet();
        devTools.send(Network.enable(Optional.empty(), Optional.empty(), Optional.empty()));

        devTools.addListener(Network.requestWillBeSent(), request -> {
            CompletableFuture<Void> future = new CompletableFuture<>();
            outstandingRequests.add(future);

            // Print the request details
            System.out.println("Request ID: " + request.getRequestId());
            System.out.println("Request URL: " + request.getRequest().getUrl());
            System.out.println("Request Method: " + request.getRequest().getMethod());
            System.out.println("Request Headers: " + request.getRequest().getHeaders());
        });

        devTools.addListener(Network.loadingFinished(), response -> {
            outstandingRequests.removeIf(CompletableFuture::isDone);
        });

        // Wait for all network requests to complete
        outstandingRequests.forEach(request -> {
            try {
                request.get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        });
    }

    @AfterMethod
    public void tearDown() {
        //driver.quit();
    }

    @Test
    public void search() {

    }
}
