CREATE TABLE inventory_orchestration.users (
    user_id INT PRIMARY KEY AUTO_INCREMENT,
    user_name VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    pwd VARCHAR(255) NOT NULL,
    role ENUM('ADMIN', 'USER') NOT NULL
);

CREATE TABLE inventory_orchestration.product (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100),
    category VARCHAR(50),
    current_stock INT,
    reorder_threshold INT,
    lead_time_days INT
);

CREATE TABLE inventory_orchestration.sales (
    id INT AUTO_INCREMENT PRIMARY KEY,
    product_id INT,
    quantity_sold INT,
    sales_date DATE,
    FOREIGN KEY (product_id) REFERENCES product(id)
);

CREATE TABLE inventory_orchestration.purchase_orders (
    id INT AUTO_INCREMENT PRIMARY KEY,
    product_id INT,
    order_date DATE,
    quantity_ordered INT,
    expected_arrival_date DATE,
    status ENUM('PENDING', 'RECEIVED'),
    FOREIGN KEY (product_id) REFERENCES product(id)
);

CREATE TABLE inventory_orchestration.logs (
  id SERIAL PRIMARY KEY,
  timestamp TIMESTAMP,
  user_id INT,
 event_type ENUM(
 'AUTH_ATTEMPT','AUTH_SUCCESS','AUTH_FAILURE',
 'JWT_GENERATE_ATTEMPT','JWT_GENERATED_SUCCESS','JWT_GENERATED_FAILURE',     
 'JWT_VALIDATION_ATTEMPT','JWT_VALIDATION_SUCCESS','JWT_VALIDATION_FAILURE',     
 'JWT_LOGIN_ATTEMPT','JWT_LOGIN_SUCCESS','JWT_LOGIN_FAILURE',
 'JWT_LOGOUT_ATTEMPT','JWT_LOGOUT_SUCCESS','JWT_LOGOUT_FAILURE',
 'JWT_EXPIRED',      
 'USER_REGISTER_ATTEMPT','USER_REGISTER_SUCCESS','USER_REGISTER_FAILED',
 'PRODUCT_CREATE','PRODUCT_UPDATE',
 'ORDER_CREATE','ORDER_PURCHASE_RECEIVED',
 'FORECAST_EXECUTE',
 'SECRET_KEY_CREATE_ATTEMPT','SECRET_KEY_CREATE_SUCCESS','SECRET_KEY_CREATE_FAILURE',
 'SECRET_KEY_ROTATE_ATTEMPT','SECRET_KEY_ROTATE_SUCCESS','SECRET_KEY_ROTATE_FAILURE', 
 'SECRET_KEY_REVOKE_ATTEMPT', 'SECRET_KEY_REVOKE_SUCCESS','SECRET_KEY_REVOKE_FAILURE',
 'SECRET_KEY_LOAD_FAILURE','SECRET_KEY_LOAD_WARNING','SECRET_KEY_LOAD_SUCCESS'
    ),
  endpoint VARCHAR(255),
  details TEXT,
  correlation_id VARCHAR(255),
  FOREIGN KEY (user_id) REFERENCES users(user_id)
);

alter table inventory_orchestration.logs
modify column event_type varChar(255)
;

