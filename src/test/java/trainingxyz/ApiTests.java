package trainingxyz;

import org.junit.jupiter.api.DisplayName; // import Displayname annotation from JUnit
import org.junit.jupiter.api.*; // import Test annotation from Junit library and use it to mark methods as test cases

import models.Product;

import static io.restassured.RestAssured.given; // import given method from Rest
import static org.hamcrest.Matchers.*;
import static org.hamcrest.MatcherAssert.assertThat; // import this for do assertions test cases for status code not equal to

// good practice to do create, update, read , delete for regression testing and also good reliable test cases

public class ApiTests {

  String baseUrl = "http://localhost:8888/"; 
  // create baseurl variable to use for every test case 

  @Test
  // create a new product
  public void testCreateProduct(){
    String endpoint= baseUrl + "product/create.php"; // create endpoint variable to use in the test case
    String body = """ 
        {
          "name": "Sweatband",
          "description": "Sweatband. Good when you train.",
          "price": 6,
          "category_id": 3
        }
        """;
        // create body variable to use in the test case in the given section
    var response = given().body(body).when().post(endpoint).then();response.log().body();

  }
  
  @Test
  // update the product created in the create product test case
  public void testUpdateProduct() {
    String endpoint = baseUrl + "product/update.php"; // create endpoint variable to use in the test case update price to 9
    String body = """
        {
          "id":1000,
          "name": "Sweatband",
          "description": "Sweatband. Good when you train.",
          "price": 9,
          "category_id": 3
        }
        """;
    // create body variable to use in the test case in the given section same as create product but added id for the existing product 
    var response = given().body(body).when().put(endpoint).then();
    response.log().body();

  }

  @Test
  public void testGetAllProducts() {
    String endpoint = baseUrl + "product/read.php"; // create endpoint variable to use in the test case

    // read all products
    var response = given().when().get(endpoint).then();
    response.log().body();

  }

  // test cases for status code assertions
  
  @Test
  @DisplayName("Test status code 200 for getting an existing product")
  public void testGetOneProductWithRightStatusCode() {
    String endpoint = baseUrl + "product/read_one.php"; // create endpoint variable to use in the test case
  
    // read one product by query param id in the given section 
    given().queryParam("id", 3).when().get(endpoint).then().log().body();

  }

  @Test
  @DisplayName("Test status code will not be 201 when getting an existing product")
  public void testGetOneProductWithWrongStatusCode() {
    String endpoint = baseUrl + "product/read_one.php"; // create endpoint variable to use in the test case

    // read one product by query param id in the given section
    given().queryParam("id", 3).when().get(endpoint).then().assertThat()
        .statusCode(not(201));

  }

  // test case for verifying fields of a retrieved product

  @Test
  @DisplayName("Test verifying fields of a retrieved product")
  public void testVerifyFieldsOfOneProduct() {
    String endpoint = baseUrl + "product/read_one.php"; // create endpoint variable to use in the test case

    // read one product by query param id in the given section
    given().queryParam("id", 3).when().get(endpoint).then().assertThat()
        .statusCode(200)
        .body("id", equalTo("3"))
        .body("name",equalTo("Grunge Skater Jeans"))
        .body("description",equalTo("Our boy-cut jeans are for men and women who appreciate that skate park fashions aren’t just for skaters. Made from the softest and most flexible organic cotton denim."))
        .body("price",not(equalTo("100.00")))
        .body("category_id",equalTo(3))
        .body("category_name",equalTo("Active Wear - Unisex"));

  }

  // test case for verifying fields in an array of products 

  @Test
  public void testVerifyFieldsInProductList() {
    String endpoint = baseUrl + "product/read.php"; // create endpoint variable to use in the test case

    // read all products
    given().when().get(endpoint).then().log().body()
    .header("Content-Type", equalTo("application/json; charset=UTF-8"))
    .assertThat().statusCode(200) // assert status code 200
    .body("records.size()",greaterThan(10))// assert that there are more than 10 products in the records array
    .body("records.id",everyItem(notNullValue())) // everyItem to check each item in the array has not null value for the given fields
    .body("records.name",everyItem(notNullValue()))
    .body("records.description",everyItem(notNullValue()))
    .body("records.price",everyItem(notNullValue()))
    .body("records.category_id",everyItem(notNullValue()))
    .body("records.category_name",everyItem(notNullValue()))
    .body("records.id[0]",equalTo(18)); // assert specific value for the first item in the array  

  }

  // test case to delete the product created in the create product test case
  @Test
  public void testDeleteOneProduct() {
    String endpoint = baseUrl + "product/delete.php"; // create endpoint variable to use in the test case
    String body = """
        {
          "id": 1000
        }
        """;

    // read one product by query param id in the given section
    var response = given().body(body).when().delete(endpoint).then();
    response.log().body();

  }
 
  @Test
  @DisplayName("Test verify headers of a retrieved product")
  public void testVerifyOneProductsHeader() {
    String endpoint = baseUrl + "product/read_one.php"; // create endpoint variable to use in the test case

    // read one product by query param id in the given section and print headers
    given().queryParam("id", 3).when().get(endpoint).then().log().headers()
    .header("Content-Type", equalTo("application/json; charset=UTF-8"));
    // assert that the Content-Type header is application/json; charset=UTF-8
    
  }

  @Test
  @DisplayName("Test Deserialized one product")
  public void getDeserializedProduct(){
    String endpoint = baseUrl + "product/read_one.php";

    var expectedProduct = new Product( // create an instance expectedProduct of the class Product and variable for how we expect the product 
      2,
      "Cross-Back Training Tank",
      "The most awesome phone of 2013!",
      299,
      2,
      "Active Wear - Women");

      // expectedProduct is what data we expect when we doing a get request 

      Product actualProduct =
        given()
          .param("id", 2)
        .when()
          .get(endpoint)
            .as(Product.class);

      // actualProduct is what data we get when we have done the get request 

      assertThat(actualProduct, equalTo(expectedProduct));

      // now we can do an simple assertion that compare that each filed is equal 
      // between variables actualProduct and ExpectedProduct 
  }
  
}
