import atu.testrecorder.ATUTestRecorder;
import atu.testrecorder.exceptions.ATUTestRecorderException;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.internal.Locatable;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Avia_ {

         public static WebDriver getDriver() {
            return driver;
        }

        public static WebDriver driver;
        //Определяем первую страницу для входа  на сайт. Сразу страница для рейса Калуга - Прага
        private static String baseurl = "https://avia.tutu.ru/offers/?passengers=100&changes=all&threeDays=false&route[0]=5731-12042018-376&class=Y";


        @BeforeClass

        public static void setUp() throws Exception {

        driver = new ChromeDriver();
        driver.manage().window().maximize();

        //Задаем неявную задержку
        driver.manage().timeouts().implicitlyWait(40, TimeUnit.SECONDS);
    }

    @Test public void Head() throws IOException, InterruptedException, ATUTestRecorderException {
        //Начинаем видеозапись
            ATUTestRecorder recorder = new ATUTestRecorder("ScriptVideos", false);
        recorder.start();
        FileWriter wri = new FileWriter("NaN.txt", true);

        WebDriverWait wait = new WebDriverWait(driver, 40);
        //Задаем ожидания загрузки страницы
        DesiredCapabilities capabilities = new DesiredCapabilities();
        capabilities.setCapability(CapabilityType.PAGE_LOAD_STRATEGY, "eager");

        //Идем на страницу входа
        driver.navigate().to(baseurl);

        driver.findElement(By.xpath("//*[@id='near_cities']/div[5]/table/tbody/tr[1]/td[1]/div/div[1]/span")).click();

        //Прокручиваем страницу, чтобы была видна на экране последняя строка таблицы
        JavascriptExecutor js = ((JavascriptExecutor) driver);
        js.executeScript("window.scrollTo(0, 200);");

        WebElement nearCities = driver.findElement(By.cssSelector("#near_cities > div:nth-child(5)"));
        List<WebElement> prices = nearCities.findElements(By.cssSelector("div.j-price.j-direct_price"));
        List<WebElement> pricesAll = nearCities.findElements(By.cssSelector("div.j-price.j-all_price"));
        List<WebElement> rows = nearCities.findElements(By.cssSelector("tr.j-row.group_row"));

        for (int i = 0; i < rows.size(); i++) {
            String row = rows.get(i).getAttribute("outerText");
            String price1 = prices.get(i).getAttribute("outerText");
            String priceAll1 = pricesAll.get(i).getAttribute("outerText");
            System.out.println(row);

            //Проверяем, что есть рейсы для данного маршрута
            if (price1.equals("нет рейсов") && priceAll1.equals("нет рейсов")) {
                System.out.println("нет рейсов");
            } else {

                //С помощью регулярных выражений проверяем, что в поле с ценами содержатся цифры
                String pattern = "([0-9])";
                Pattern p = Pattern.compile(pattern);
                String price2 = prices.get(i).getAttribute("outerText");
                String priceAll2 = pricesAll.get(i).getAttribute("outerText");

                System.out.println(price2);
                Matcher mPrice = p.matcher(price2);
                Matcher mPriceAll = p.matcher(priceAll1);
                StringBuilder sbPrice = new StringBuilder();
                StringBuilder sbPriceAll = new StringBuilder();

                while (mPrice.find()) {
                    String nomber;
                    nomber = price2.substring(mPrice.start(), mPrice.end());
                    sbPrice.append(nomber);
                }

                while (mPriceAll.find()){
                    String nomberAll;
                    nomberAll = priceAll2.substring(mPriceAll.start(), mPriceAll.end());
                    sbPriceAll.append(nomberAll);
                }
                System.out.println("___" + sbPrice + "______" + sbPriceAll);
                if (sbPrice.length() == 0 | sbPriceAll.length() == 0) {
                    System.out.println("Price Error");
                }
                System.out.println();
                }
        }
    }

    @After
    public void stop(){
        driver.quit();
        driver = null;
    }
}