# Cookbook
This project was developed for CSCI 455 - Senior Project under the guidance of Dr. Frank Lee. The main goal of this project was to write a software that is able to extract only the instructions of certain recipe websites.

![image](https://user-images.githubusercontent.com/48269287/211882522-69ee4458-f058-45fd-ab6c-6a5b30af0b71.png)




The software utilizes targeted scraping, working only on certain whitelisted domains of which unique methods of extraction were written for, as different websites have different layouts in the way they display data.

A more complete breakdown of the project can be found in both the presentation file and the final report. Both are included in this repo, along with all the other documents that were created, including proposals, design documents, weekly reports, etc. The css and html file created by Ruchira Bunga was also included, as a future reference if it can be implemented in later versions.

# How it works

![image](https://user-images.githubusercontent.com/48269287/211867496-cb46e70d-1de4-4425-a7ca-4cd251a29230.png)

The basic structure of the driver program is listed above, depicted in three states.
* <b>STAGE_ONE</b>   : Acts as the main panel, where the user can input the link to the recipe
* <b>STAGE_TWO</b>   : Acts as a configuration, where the user can login or register, with also a built in database configurator
* <b>STAGE_THREE</b> : Acts as a user portal, where the user can retrieve saved recipes and search through them

A more complete flowchart of the program flow can be found below:

![image](https://user-images.githubusercontent.com/48269287/211869491-80136a9e-7531-4a23-a0af-19b1f89e546c.png)

The flow of the program relies on user input, and thus throughout the code switch-case statements were preferred over if-statements. Furthermore, in an attempt to take advantage of Java's OOP properties, the code was designed to be somewhat 'modular', with a lot of the square or processing blocks in the flowchart being external classes in their own packages. The purpose of this was twofold:

1) To ensure that if there were errors, we can isolate the faults quickly and debug only that part of the code

2) If we wanted to reuse parts of the code, the modular design makes it so we can just import the classes

For example, we implemented a password hashing using SHA-256 hashing that takes advantage of Java's built in MessageDigest feature. This class can stand on its own, and if we need to hash more things using the same property then we can call upon the class again.

# Structure

![image](https://user-images.githubusercontent.com/48269287/212480702-c7fa8627-1454-49a3-937c-726701b7e116.png)

6 classes were written for this project. Aside from the main driver, we have:

1) <b>CookbookScraper</b>
* scrapeLink(String) -> takes in the link as a String as the input, and scrapes it for the instructions. It is then stored as an ArrayList and returned
* printRecipe() -> a simple script to print all the recipes as stored in the above ArrayList
* fileRecipe() -> as a database cannot store an ArrayList, the fileRecipe() method takes the ArrayList input and encodes iti n a way that we can then store as a text in the database
* splitFiled() -> essentially a reverse of fileRecipe(), it reads the text input and splits it into an ArrayList

2)  <b>PasswordControl</b>
* encodePassword -> 
