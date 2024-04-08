package com.currency.currencyconvertor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

public class currency {

    static Scanner scanner = new Scanner(System.in);
    static FileWriter currencyFileList;// new FileWriter("FavoriteCurrencyList.txt");
    public static final String BASE_URL = "https://cdn.jsdelivr.net/npm/@fawazahmed0/currency-api@latest/v1/currencies/";
    public static final String ENDPOINT = ".json";
    static Map<String, Object> map = new HashMap<>();
    // this object is used for executing requests to the (REST) API
    static CloseableHttpClient httpClient = HttpClients.createDefault();

    public static void liveCurrencyConvertor() throws IOException {

        try {

            System.out.println("type the country currency code conversion from");
            String source = scanner.next();
            System.out.println("Type the country currency code conversion To");
            String destination = scanner.next();
            System.out.println("Enter the Amount to converted it");
            BigDecimal quantity = scanner.nextBigDecimal();
            System.out.println("Fetching The Live rates.....");

            HttpGet get = new HttpGet(BASE_URL + source + ENDPOINT);

            CloseableHttpResponse response = httpClient.execute(get);

            HttpEntity entity = response.getEntity();

            JSONObject exchangeRates = new JSONObject(EntityUtils.toString(entity));

            System.out.println("Live Exchange Rates ");

            map = exchangeRates.getJSONObject(source).toMap();

            String str = map.get(destination).toString();
            BigDecimal bd = new BigDecimal(str);

            System.out.println("******************************************************************");

            System.out.println("currency conversion from amount " + quantity + " " + source + " to " + destination + " is :" + bd.multiply(quantity) + ", since " + exchangeRates.get("date"));

            System.out.println("******************************************************************");

        } catch (NullPointerException e) {

            System.out.println("please enter the correct country code if you dont know, you can check it at option 5");
        }
    }
    //return bd.multiply(quantity);

    public static BigDecimal fetchingCurrencyData(String convertFrom, String convertTo) throws IOException {
        BigDecimal value = new BigDecimal(0);
        try {
            String source = convertFrom;
            String destination = convertTo;
            System.out.println("Data is adding.....");
            HttpGet get = new HttpGet(BASE_URL + source + ENDPOINT);

            CloseableHttpResponse response = httpClient.execute(get);
            HttpEntity entity = response.getEntity();

            JSONObject exchangeRates = new JSONObject(EntityUtils.toString(entity));

            map = exchangeRates.getJSONObject(source).toMap();

            String str = map.get(destination).toString();
            BigDecimal bd = new BigDecimal(str);

            System.out.println("******************************************************************");
            System.out.println("Your favorite Currency is added want to add more (:");
            System.out.println("******************************************************************");
            value = bd;

        } catch (Exception e) {
            System.out.println("please enter the correct country code if you dont know, you can check it at option 5");
        }
        return value;
    }

    public static void addFavoriteCurrency() throws IOException {
        currencyFileList = new FileWriter("FavoriteCurrencyList.txt", true);
        System.out.println("Entert the Currency you want to add in favorite");
        String currencyConversionFromName = scanner.next();
        System.out.println("Entert the currency you want to convert it");
        String currencyConversionToName = scanner.next();

        currencyFileList.write(currencyConversionFromName + ":" + currencyConversionToName + " => " + fetchingCurrencyData(currencyConversionFromName, currencyConversionToName));
        currencyFileList.write(System.lineSeparator());
        currencyFileList.close();
    }

    public static void viewFavoriteCurrencyList() throws IOException {
        File file = new File("FavoriteCurrencyList.txt");

        BufferedReader br = new BufferedReader(new FileReader(file));

        // Read the data from the file line by line
        String line;
        System.out.println("******************************************************************");
        while ((line = br.readLine()) != null) {
            System.out.println(line);
        }
        System.out.println("******************************************************************");

    }

    public static void updateFavoriteCurrencyValue() throws IOException {
        System.out.println("which country code currency want to update ???!!!");
        String updateFromCurrency = scanner.next();
        System.out.println("in respect to which Country code ");
        String updateToCurrency = scanner.next();
        System.out.println("What is the current updated Value ??");
        BigDecimal updateCurrencyValue = scanner.nextBigDecimal();

        File file = new File("FavoriteCurrencyList.txt");

        RandomAccessFile raf = new RandomAccessFile(file, "rw");
        int index;
        String currency;
        String fileContent;
        boolean found = false;
        String stringLineInFile = updateFromCurrency + ":" + updateToCurrency;
        while (raf.getFilePointer() < raf.length()) {

            fileContent = raf.readLine();

            String[] fileContentSplit = fileContent.split("=>");

            String currencyName = fileContentSplit[0].trim();
            String currencyValue = fileContentSplit[1].trim();

            if (stringLineInFile.equals(currencyName)) {
                found = true;
                break;
            }
        }
        if (found == true) {
            File tempFile = new File("temp.txt");
            RandomAccessFile rafTemp = new RandomAccessFile(tempFile, "rw");

            raf.seek(0);
            while (raf.getFilePointer() < raf.length()) {
                fileContent = raf.readLine();
                index = fileContent.indexOf("=");
                currency = fileContent.substring(0, index - 1);
                if (currency.equals(stringLineInFile)) {
                    fileContent = currency + " => " + String.valueOf(updateCurrencyValue);

                }
                rafTemp.writeBytes(fileContent);
                rafTemp.writeBytes(System.lineSeparator());
            }
            raf.seek(0);
            rafTemp.seek(0);

            while (rafTemp.getFilePointer() < rafTemp.length()) {
                raf.writeBytes(rafTemp.readLine());
                raf.writeBytes(System.lineSeparator());
            }
            raf.setLength(rafTemp.length());
            rafTemp.close();
            raf.close();

            tempFile.delete();
            System.out.println("******************************************************************");
            System.out.println("value is updated");
            System.out.println("******************************************************************");

        } else {
            raf.close();
            System.out.println("value not found");
        }

    }

    public static void deleteFavoriteCurrencyValue() throws IOException {
        System.out.println("which country code currency want to Delete ???!!!");
        String updateFromCurrency = scanner.next();
        System.out.println("in respect to which Country code ");
        String updateToCurrency = scanner.next();

        File file = new File("FavoriteCurrencyList.txt");

        RandomAccessFile raf = new RandomAccessFile(file, "rw");
        int index;
        String currency;
        String fileContent;
        boolean found = false;
        String stringLineInFile = updateFromCurrency + ":" + updateToCurrency;
        while (raf.getFilePointer() < raf.length()) {

            fileContent = raf.readLine();

            String[] fileContentSplit = fileContent.split("=>");

            String currencyName = fileContentSplit[0].trim();
            String currencyValue = fileContentSplit[1].trim();

            if (stringLineInFile.equals(currencyName)) {
                found = true;
                break;
            }
        }
        if (found == true) {
            File tempFile = new File("temp.txt");
            RandomAccessFile rafTemp = new RandomAccessFile(tempFile, "rw");

            raf.seek(0);
            while (raf.getFilePointer() < raf.length()) {
                fileContent = raf.readLine();
                index = fileContent.indexOf("=");
                currency = fileContent.substring(0, index - 1);
                if (currency.equals(stringLineInFile)) {
                    continue;

                }
                rafTemp.writeBytes(fileContent);
                rafTemp.writeBytes(System.lineSeparator());
            }
            raf.seek(0);
            rafTemp.seek(0);

            while (rafTemp.getFilePointer() < rafTemp.length()) {
                raf.writeBytes(rafTemp.readLine());
                raf.writeBytes(System.lineSeparator());
            }
            raf.setLength(rafTemp.length());
            rafTemp.close();
            raf.close();

            tempFile.delete();
            System.out.println("******************************************************************");
            System.out.println("value is deleted");
            System.out.println("******************************************************************");

        } else {
            raf.close();
            System.out.println("value not found");
        }
    }

    public static void getCountryCode() throws IOException {
        System.out.println("type the country name of which you want to know the country code");
        String fromCountryCode = scanner.next();
        System.out.println("type the other country name of which you want to know the country code");
        String toCountryCode = scanner.next();
        HashMap<String, String> countryCodes = new HashMap<>();
        File file = new File("currencyCodes.txt");
        RandomAccessFile reader = new RandomAccessFile(file, "r");
        String line;
        while ((line = reader.readLine()) != null) {
            int index = line.indexOf("-");
            if (line.substring(0, index - 1).toLowerCase().equals(fromCountryCode)) {
                System.out.println("country code of " + fromCountryCode + " is" + line.substring(index + 1));
                break;
            }
        }
        reader.seek(0);
        while ((line = reader.readLine()) != null) {
            int index = line.indexOf("-");
            if (line.substring(0, index - 1).toLowerCase().equals(toCountryCode)) {
                System.out.println("country code of " + toCountryCode + " is" + line.substring(index + 1));
                break;

            }
        }
        reader.close();
    }

    public static void main(String args[]) throws IOException {

        while (true) {
            System.out.println("1. For the live Currency Conversion");
            System.out.println("2. For Adding your Favorite Currency");
            System.out.println("3. For Viewing Your Favorite Currency");
            System.out.println("4. For any updation in currency values!!!");
            System.out.println("5. For country code");
            System.out.println("6. Exit");
            int options = scanner.nextInt();
            if (options == 1) {
                System.out.println("did you Know country code Y|N");
                String asking = scanner.next();
                if (asking.equals("N") || asking.equals("n")) {
                    getCountryCode();
                } else {
                    liveCurrencyConvertor();
                }
            } else if (options == 2) {
                addFavoriteCurrency();

            } else if (options == 3) {
                viewFavoriteCurrencyList();

            }
            while (options == 4) {
                System.out.println("   1. update the value of any currency");
                System.out.println("   2. Delete the currency in the list of Favorite");
                System.out.println("   3. if want to go back into main menu ");
                int choice = scanner.nextInt();
                if (choice == 1) {
                    updateFavoriteCurrencyValue();
                }
                if (choice == 2) {
                    deleteFavoriteCurrencyValue();
                }
                if (choice == 3) {
                    break;
                }

            }
            if (options == 6) {
                break;
            } else if (options == 5) {
                getCountryCode();
            } else {
                System.out.println("please choose option from the menu !!!");
            }
        }
    }
}
