package com.test.steps;

import com.test.ObjectRepository;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

import java.util.ArrayList;
import java.util.List;

import static com.test.pages.HomePage.matchingOrNot;
import static junit.framework.TestCase.*;

public class CarDeatailStepdefs extends ObjectRepository {
    private List<String> regNumbersFromInputFile = new ArrayList<>();

    @Given("^user reads the input file \"([^\"]*)\" and extracts the car registration numbers$")
    public void userReadsTheInputFileAndExtractsTheCarRegistrationNumbers(String inputFileName) throws Throwable {
        regNumbersFromInputFile = utilPage().getRegNumbersFromInputFile(inputFileName);
        System.out.println(regNumbersFromInputFile);
    }

    @When("^user enters the extracted registered numbers into cazoo website and saves the details of$")
    public void userEntersTheExtractedRegisteredNumbersIntocazooWebsiteAndSavesTheDetailsOfMAKEMODEL() throws Exception{
        navigate().toCazooWebSite();
        homePage().getCarDetails(regNumbersFromInputFile);
    }

    @Then("^compared the details with the output file name \"([^\"]*)\" and display any mismatches exists$")
    public void comparedTheDetailsWithTheOutputFileNameAndDisplayAnyMismatchesExists(String outputfile) throws Throwable {
        homePage().isCarDetailsMatchWithOutPutFile(outputfile);
        assertFalse(matchingOrNot.contains(false));
    }
}
