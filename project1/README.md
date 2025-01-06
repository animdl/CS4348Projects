# CS4348 Project 1

## General Info
- Supports ASCII
- Commands are not case sensitive
- Including the class files, in case compilation does not work for some reason

## Compiled on openjdk 17 
1. ```javac driver.java encrypt.java log.java```
2. ```java driver <log_file>```
    
## log.java
### Description:
Simple File Writer
### Input: ```ACTION <Multi Line Message>```
### Output: 
- Console Output: None

- File Output: In the form ```"yyyy-mm-dd hh:mm" [ACTION] <Multi Line Message>```

## encrypt.java
### Description: 
Vigenere cipher encryption and decryption
### Input: ```encrypt/decrypt <Multi Line Input>```
### Output:
- Console Output: In the form ```RESULT/ERROR <Multi Line Message>```

- File Output: None

## driver.java
### Description: 
Creates, Manages, and Kills log.java and encrypt.java programs as Java Processes
### Input: ```command``` then ```input```
- command can be password, encrypt, decrypt, history, quit
### Output: 
- Passes input from User -> Programs

- Captures output from Programs -> User

    - Console output from encrypt.java

    - File output from log.java