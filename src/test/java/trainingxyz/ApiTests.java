package trainingxyz;

// import Displayname annotation from JUnit
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
  public void CreateProduct(){
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
  public void UpdateProduct() {
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
  public void GetAllProducts() {
    String endpoint = baseUrl + "product/read.php"; // create endpoint variable to use in the test case

    // read all products
    var response = given().when().get(endpoint).then();
    response.log().body();

  }

  // test cases for status code assertions
  
  @Test
  @DisplayName("Verify status code is 200 when getting an existing product")
  public void StatusCodeIs200() {
    String endpoint = baseUrl + "product/read_one.php"; // create endpoint variable to use in the test case
  
    // read one product by query param id in the given section 
    given().queryParam("id", 3).when().get(endpoint).then().log().body();

  }

  @Test
  @DisplayName("Verify status code is not 201 when getting an existing product")
  public void statusCodeNot201() {
    String endpoint = baseUrl + "product/read_one.php"; // create endpoint variable to use in the test case

    // read one product by query param id in the given section
    given().queryParam("id", 3).when().get(endpoint).then().assertThat()
        .statusCode(not(201));

  }

  // test case for verifying fields of a retrieved product

  @Test
  @DisplayName("Verify that all fields of a retrieved product is correctly")
  public void ProductFields() {
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
  @DisplayName("Verify that fields of products list are not empty")
  public void ProductListFields() {
    String endpoint = baseUrl + "product/read.php"; // create endpoint variable to use in the test case

    // read all products
    given().when().get(endpoint).then().log().body()
    .body("records.size()",greaterThan(10))// assert that there are more than 10 products in the records array
    .body("records.id",everyItem(notNullValue())) // everyItem to check each item in the array has not null value for the given fields
    .body("records.name",everyItem(notNullValue()))
    .body("records.description",everyItem(notNullValue()))
    .body("records.price",everyItem(notNullValue()))
    .body("records.category_id",everyItem(notNullValue()))
    .body("records.category_name",everyItem(notNullValue()))
    // .body("records.id[0]",equalTo(1000)) // assert specific value for the first item in the array  
    .body("records.id", hasItem(1));
  }

  // test case to delete the product created in the create product test case
  @Test
  public void DeleteProduct() {
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
  @DisplayName("Verify Content-Type header of product")
  public void ContentTypeHeader() {
    String endpoint = baseUrl + "product/read_one.php"; // create endpoint variable to use in the test case

    // read one product by query param id in the given section and print headers
    given().queryParam("id", 3).when().get(endpoint).then().log().headers()
    .header("Content-Type", equalTo("application/json"));
    // assert that the Content-Type header is application/json; charset=UTF-8
    
  }

  @Test
  @DisplayName("Verify deserialization of a product is correct for all fields")
  public void productDeserialization(){
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


  @Test
  @DisplayName("Verify status code, headers, and all fields of the vitamin product")
  public void VitaminProductDetails() {
    String endpoint = baseUrl + "product/read_one.php"; // create endpoint for the request 

    // create variable to restore the response 
    var response = given().
    param("id", 18)
    .when()
    .get(endpoint);
    response.then().assertThat().statusCode(200);
    response.then().assertThat().header("Content-Type", equalTo("application/json"));
  
    // use response variable and convert it to a javaobject from Json 

    Product actualProduct=response.as(Product.class);

    Product expectedProduct = new Product( // create instance to class product to variable Expected product 
        18,
        "Multi-Vitamin (90 capsules)",
        "A daily dose of our Multi-Vitamins fulfills a day’s nutritional needs for over 12 vitamins and minerals.",
        10.00,
        4,
        "Supplements");

    assertThat(actualProduct, equalTo(expectedProduct)); 
    // compare that values match between the actualProduct and the expectedProduct

   
  }
  
}
