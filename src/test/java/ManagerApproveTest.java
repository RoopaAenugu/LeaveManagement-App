import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class ManagerApproveTest
{
        public static void main(String[] args) {
            // Set up WebDriver
            WebDriverManager.chromedriver().setup();
            WebDriver driver = new ChromeDriver();
            // Navigate to the application
            driver.get("http://localhost:8080/LeaveManagementApp");

            // Perform login
            driver.findElement(By.id("emailId")).sendKeys("roopa@gmail.com");
            driver.findElement(By.id("password")).sendKeys("123");
            driver.findElement(By.id("loginButton")).click();

            // Navigate to the leave application page
            // Wait for and click on the "Apply Leave" link
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10)); // 10 seconds wait
            wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//a[contains(text(), 'My Team Leaves')]"))).click();

            // Fill out the leave application form
            driver.findElement(By.id("reject-btn-8")).click();
            driver.findElement(By.id("profileContainer")).click();
            // Wait for the profile dropdown to be visible
            // Click on the logout icon
            driver.findElement(By.id("logoutButton")).click(); // Clear any existing text


        }
    }


