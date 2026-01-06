package trainingxyz;

import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;

// good practice to do create, update, read , delete for regression testing and also good reliable test cases

public class ApiTests {

  String baseUrl = "http://localhost:8888/"; 
  // create baseurl variable to use for every test case 

  @Test
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
  
  @Test
  public void testGetOneProduct() {
    String endpoint = baseUrl + "product/read_one.php"; // create endpoint variable to use in the test case
  
    // read one product by query param id in the given section 
    var response = given().queryParam("id", 1000).when().get(endpoint).then();
    response.log().body();

  }

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
 
  
}
