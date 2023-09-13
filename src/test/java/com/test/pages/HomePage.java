package com.test.pages;

import com.test.configs.ConfigFileReader;
import org.openqa.selenium.By;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

public class HomePage extends UtilPage {

    private static final By regTextField = By.cssSelector("input[id='vrm']");
    private static final By freeCarCheckButton = By.xpath("//span[contains(text(),'Get started')]/..");
    public static final By acceptAllCookiesButton = By.xpath("//span[contains(text(),'Accept All')]/..");
    public static List<Boolean> matchingOrNot = new ArrayList<>();
    private static Map<String, List<String>> actualCarDetails = new HashMap<>();

    private static HomePage homePage;

    public static HomePage getInstance() {
        return (homePage == null) ? new HomePage() : homePage;
    }

    private void enterRegNumber(String txt) {

        enterText(regTextField, txt);
    }

    private void clickCheckBtn() {
        findElement(freeCarCheckButton).click();
    }

    private void searchWithRegNumber(String txt) {
        enterRegNumber(txt);
        clickCheckBtn();
    }

    private List<String> getCarProperties() {
        String h1 = findElement(By.cssSelector("h1")).getText();
        List<String> params = Arrays.asList(h1.split(" "));
        String h2 = findElement(By.xpath("//h2[2]")).getText();
        List<String> paramsH2 = Arrays.asList(h2.split("\\|"));
        List<String> formattedH2 = paramsH2.stream().map(String::trim).collect(Collectors.toList());
        List<String> newList = new ArrayList<String>(formattedH2);
        newList.add(params.get(0));
        String model = params.size()>2?params.get(1).concat(" " +params.get(2)):params.get(1);
        newList.add(model);
        return newList;
    }

    public void getCarDetails(List<String> regNumbersFromInputFile) {
        regNumbersFromInputFile.forEach(regNo -> {
            List<String> carVals = new ArrayList<>();
            searchWithRegNumber(regNo);
            if (vehicleNotFound())
                carVals.add("We couldn't find a car with that registration.");
            else {
                carVals.addAll(getCarProperties());
                actualCarDetails.put(regNo, carVals);
            }
            Navigate.getInstance().toCazooSiteWithoutCookies();
        });
    }

    private boolean vehicleNotFound()  {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return driver.getPageSource().contains("We couldn't find a car with that registration.");
    }

    public void isCarDetailsMatchWithOutPutFile(String fileName) throws Exception {
        String path = ConfigFileReader.getInstance().getFilePath();
        File testFile = new File(path + fileName);
        List<Car> cars = CarReader.readFile(testFile);
        List<String> regNumbers = new ArrayList<>(actualCarDetails.keySet());
        regNumbers.forEach(regNumber -> {
            Car expectedCarDetails;
            try {
                expectedCarDetails = cars.stream().filter(car -> car.getREGISTRATION().equals(regNumber.replace(" ", ""))).collect(Collectors.toList()).get(0);
                isCarDetailsMatching(regNumber, expectedCarDetails);
            } catch (IndexOutOfBoundsException e) {
                System.out.println("\n" + regNumber + " Details: \n");
                System.out.println(regNumber + " details are not in output file");
                if (actualCarDetails.get(regNumber).size() < 2)
                    System.out.println(regNumber + " details are not found in website");
            }
        });
    }

    private void isCarDetailsMatching(String regNumber, Car expectedCarDetails) {
        Map<String, Boolean> matched = new HashMap<>();
        boolean make = actualCarDetails.get(regNumber).get(2).equalsIgnoreCase(expectedCarDetails.getMAKE());
        boolean model = expectedCarDetails.getMODEL().contains(actualCarDetails.get(regNumber).get(3));
        boolean description = expectedCarDetails.getMODEL().contains(actualCarDetails.get(regNumber).get(0));
        matchingOrNot.addAll(Arrays.asList(make,model,description));
        matched.put("make", make);
        matched.put("model", model);
        matched.put("description", description);
        System.out.println("\n" + regNumber + " Details: \n");
        matched.keySet().forEach(key -> {
            if (!matched.get(key))
                System.out.println("Car having the " + regNumber + " " + key + " is NOT matched in website");
            else
                System.out.println("This Car " + key + " is same as output file comparing with cazoo website");
        });
    }
}
