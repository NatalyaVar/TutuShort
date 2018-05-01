import atu.testrecorder.ATUTestRecorder;
import atu.testrecorder.exceptions.ATUTestRecorderException;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.io.FileHandler;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.io.File;
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

        //Определяем первую страницу для входа  на сайт. Сразу страница для рейса Калуга - Прага. Дату меняю вручную
        private static String baseurl = "https://avia.tutu.ru/offers/?passengers=100&changes=all&threeDays=false&route[0]=5731-30082018-376&class=Y";

        @BeforeClass
        public static void setUp() throws Exception {

        driver = new ChromeDriver();
        driver.manage().window().maximize();

        //Задаем неявную задержку
        driver.manage().timeouts().implicitlyWait(40, TimeUnit.SECONDS);
    }

    @Test public void Prices () throws IOException, InterruptedException, ATUTestRecorderException {

        //Начинаем видеозапись
            ATUTestRecorder recorder = new ATUTestRecorder("ScriptVideos", false);
        recorder.start();

        WebDriverWait wait = new WebDriverWait(driver, 40);

        //Идем на страницу входа
        driver.navigate().to(baseurl);

        //Рейсы соседних городов
        driver.findElement(By.cssSelector("li.service_item.m-near_cities")).click();

        //Соседние города с Калугой и Прагой
        driver.findElement(By.xpath("//*[@id='near_cities']/div[5]/table/tbody/tr[1]/td[1]/div/div[1]/span")).click();

        //Прокручиваем страницу, чтобы была видна на экране последняя строка таблицы
        JavascriptExecutor js = ((JavascriptExecutor) driver);
        js.executeScript("window.scrollTo(0, 200);");

        //Делаем скриншот, чтобы была видна таблица с багами
        File scrFile = ((TakesScreenshot)driver).getScreenshotAs(OutputType.FILE);
        File screenShotFile = new File("Screen_near_cities.png");
        FileHandler.copy(scrFile, screenShotFile);

        //Проверяем содержимое полей, в которых указаны цены - должны содержать числа или "нет рейсов"
        VerifyPrices();

          recorder.stop();
    }

    @After
    public void stop(){
        driver.quit();
        driver = null;
    }

    private void VerifyPrices () throws IOException {

        FileWriter wri = new FileWriter("NaN.txt", true);

        //Таблица с рейсами и ценами
        WebElement nearCities = driver.findElement(By.cssSelector("#near_cities > div:nth-child(5)"));

        //Список строк из таблицы
        List<WebElement> rows1 = nearCities.findElements(By.cssSelector("tr.j-row.group_row"));

        //Проверяем цены в каждой строке
        for (int i = 0; i < rows1.size(); i++) {
            List<WebElement> pricesAll = nearCities.findElements(By.cssSelector("div.j-price.j-all_price"));
            List<WebElement> rows = nearCities.findElements(By.cssSelector("tr.j-row.group_row"));
            String row = rows.get(i).getAttribute("outerText");
            List<WebElement> prices = nearCities.findElements(By.cssSelector("div.j-price.j-direct_price"));
            String price1 = prices.get(i).getAttribute("outerText");
            String priceAll1 = pricesAll.get(i).getAttribute("outerText");
            wri.write(row);

            //Проверяем, что есть рейсы для данного маршрута
            if (price1.equals("нет рейсов") && priceAll1.equals("нет рейсов")) {
                wri.write("Нет рейсов" + "\n");
            } else {
                //С помощью регулярных выражений проверяем, что в поле с ценами содержатся цифры
                String pattern = "([0-9])";
                Pattern p = Pattern.compile(pattern);
                String price2 = prices.get(i).getAttribute("outerText");
                String priceAll2 = pricesAll.get(i).getAttribute("outerText");

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
                if (sbPrice.length() == 0 | sbPriceAll.length() == 0) {
                    wri.write("Price error__" + sbPrice + "______" + sbPriceAll + "\n");
                }
            }
        }
        wri.flush();
        wri.close();
    }
}