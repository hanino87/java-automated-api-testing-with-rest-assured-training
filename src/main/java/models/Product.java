package models;
// models to create a product with fields from the database
public record Product( // constructor of class Product
    int id, // is a field that the produkt gettting after it´s created
    String name,
    String description,
    double price,
    int category_id,
    String category_name) { // is a field that the produkt gettting after it´s created

  public Product(String name, String description, double price, int category_id) {
    this(0, name, description, price, category_id, null);
  }

}