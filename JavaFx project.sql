create database if not exists gobuy;
use GoBuy;

CREATE TABLE Users (
    name VARCHAR(100) primary key,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    phone VARCHAR(15),
    address TEXT,
    createdAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE Products (
    productID INT AUTO_INCREMENT PRIMARY KEY,
--     productName
    name VARCHAR(100) NOT NULL,
    description TEXT,
    price DECIMAL(10, 2) NOT NULL,
    stock INT NOT NULL,
    category VARCHAR(50),
    imageURL VARCHAR(255),
    createdAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updatedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE Cart (
    cartID INT AUTO_INCREMENT PRIMARY KEY,
--     username
    name VARCHAR(100),
    productID INT NOT NULL,
    quantity INT NOT NULL,
    addedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (name) REFERENCES Users(name) ON DELETE CASCADE,
    FOREIGN KEY (productID) REFERENCES Products(productID) ON DELETE CASCADE
);

CREATE TABLE Orders (
    orderID INT AUTO_INCREMENT PRIMARY KEY,
     name VARCHAR(100),
    totalAmount DECIMAL(10, 2) NOT NULL,
    orderDate TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    paymentStatus ENUM('Pending', 'Paid', 'Failed') DEFAULT 'Pending',
    shippingAddress TEXT,
    FOREIGN KEY (name) REFERENCES Users(name) ON DELETE CASCADE
);

CREATE TABLE OrderItems (
    orderItemID INT AUTO_INCREMENT PRIMARY KEY,
    orderID INT NOT NULL,
    productID INT NOT NULL,
    quantity INT NOT NULL,
    price DECIMAL(10, 2) NOT NULL,
    FOREIGN KEY (orderID) REFERENCES Orders(orderID) ON DELETE CASCADE,
    FOREIGN KEY (productID) REFERENCES Products(productID) ON DELETE CASCADE
);

CREATE TABLE Invoices (
    invoiceID INT AUTO_INCREMENT PRIMARY KEY,
    orderID INT NOT NULL,
    invoiceDate TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    totalAmount DECIMAL(10, 2) NOT NULL,
    FOREIGN KEY (orderID) REFERENCES Orders(orderID) ON DELETE CASCADE
);

INSERT INTO products(name,description,price,stock,category,imageURL) VALUES("Pan Cake","This is a Pan Cake",50,3,"Bakery","/Images/PanCake.jpg");

delete FROM products WHERE productID=1;
select * from products;


INSERT INTO products(name,description,price,stock,category,imageURL) VALUES("Burger","This is a Burger",50,30,"Bakery","/Images/Burger.jpg");
INSERT INTO products(name,description,price,stock,category,imageURL) VALUES("Cold Coffee","This is a Coffee",30,10,"Liquid","/Images/Coffee.jpg");

select * from products;
select * from cart;

SELECT (quantity,Products.name,price,imageURL) FROM products INNER JOIN cart ON cart.productID=products.productID where cart.name="Kirtan";