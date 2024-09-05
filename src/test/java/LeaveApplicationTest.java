import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class LeaveApplicationTest {
    public static void main(String[] args) {
        // Set up WebDriver
        WebDriverManager.chromedriver().setup();
        WebDriver driver = new ChromeDriver();


            // Navigate to the application
            driver.get("http://localhost:8080/LeaveManagementApp");

            // Perform login
            driver.findElement(By.id("emailId")).sendKeys("ashu@gmail.com");
            driver.findElement(By.id("password")).sendKeys("123");
            driver.findElement(By.id("loginButton")).click();

            // Navigate to the leave application page
            // Wait for and click on the "Apply Leave" link
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10)); // 10 seconds wait
            wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//a[contains(text(), 'Apply Leave')]"))).click();

            // Fill out the leave application form
        driver.findElement(By.name("fromDate")).clear();  // Clear any existing text
        driver.findElement(By.name("fromDate")).sendKeys("09-05-2024");

        driver.findElement(By.name("toDate")).clear();  // Clear any existing text
        driver.findElement(By.name("toDate")).sendKeys("09-06-2024");
            driver.findElement(By.name("reason")).sendKeys("trip");

            // Select leave type from dropdown
            Select dropdown = new Select(driver.findElement(By.id("leaveType")));
            dropdown.selectByVisibleText("Casual Leave");

            // Submit the form
            driver.findElement(By.id("applyBtn")).click();
       // wait.until(ExpectedConditions.presenceOfElementLocated(By.id("successMessage"))); // Adjust locator based on your application's response

        // Navigate to profile page
        driver.findElement(By.id("profileContainer")).click();
        // Wait for the profile dropdown to be visible
        //wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("profileDropdown")));

        // Click on the logout icon
        driver.findElement(By.id("logoutButton")).click(); // Adjust the locator based on your logout icon's id or another attribute

        // Optionally, wait for the login page to ensure successful logout
        wait.until(ExpectedConditions.urlToBe("http://localhost:8080/LeaveManagementApp")); // Adjust URL as necessary


    }
}
