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

<b>CookbookScraper</b>
* scrapeLink(String) -> takes in the link as a String as the input, and scrapes it for the instructions. It is then stored as an ArrayList and returned
* printRecipe() -> a simple script to print all the recipes as stored in the above ArrayList
* fileRecipe() -> as a database cannot store an ArrayList, the fileRecipe() method takes the ArrayList input and encodes iti n a way that we can then store as a text in the database
* splitFiled() -> essentially a reverse of fileRecipe(), it reads the text input and splits it into an ArrayList

<b>PasswordControl</b>
* encodePassword -> this method simply converts a string into a byte using SHA-256 algorithm, provided using a built in MessageDigest function with Java. However, as it outputs it into bytes, it still needs to be converted into a hexadecimal string, which is where the next method comes
* byteToHexConverter -> converts a byte value into a 64 digit hexadecimal
* inputPassword -> we wanted to have at least a way to obscure user inputs when enterring the password, by setting it in its own class we also attempted some amount of obfuscation and encapsulation. This also allowed us to freely switch between Scanner input (for debugging, as IDE does not allow console reading) and the obscured console. In the future we should take an additional String to make it easier to switch (so we only have to change one variable instead of commenting out a block)

<b>AccountRegister</b> and <b>AccountLogin</b>
These two methods work similarly, so it is easier to explain them at the same time. They both take username, password, and a database connection and retrieves information from the database. The difference lies in what code they return, and also that AccountRegister updates the database if it does not find any conflict (already registered username). 

There is a security vulnerability here in that we are using Statements instead of PreparedStatements, which leaves potential for SQL injection attacks.

<b>DatabaseControl</b>
This was a rewrite of an old code to essentially connect to a database. Learning from previous experience, I wanted to focus on being able to reuse this code in case I need to do another database connected project (at least at this scale) without having to figure out the jdbc connection code. Having the username, password, and URL be part of the arguments also allowed changing connection on the fly as opposed to having it hardcoded to the code. It adds a little potential for adjustment.

<b>Demo</b>
The demo is the biggest part of the code and serves as the driver, it was written when we realized we couldn't find enough time to figure out how to properly attach Java with a web front end due to issues in our research and outdated sources.
* Main -> The main method exists to handles the switching of the stages. This is to ensure that each stage stays isolated, and any bug that may exist only exist in that stage and is easier to patch. Each following method is ran and returns a value as to what stage each part should transition to.
* printIntro -> This was written to fulfill the syllabus requirement of the code, containing attribution and a reference to the logo designed for the project in ascii
* printSupported -> This contains the websites that are currently supported as we used a whitelisted method. This serves as a secondary reminder of what websites we have written for and is stored in a hashtable.
* sendRecipe -> 
* StageOne ->
* StageTwo
* StageThree


