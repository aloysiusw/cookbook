# Cookbook

![image](https://user-images.githubusercontent.com/48269287/211882522-69ee4458-f058-45fd-ab6c-6a5b30af0b71.png)


This project was developed for CSCI 455 - Senior Project under the guidance of Dr. Frank Lee. The main goal of this project was to write a software that is able to extract only the instructions of certain recipe websites.

The software utilizes targeted scraping, working only on certain whitelisted domains of which unique methods of extraction were written for, as different websites have different layouts in the way they display data.

# How it works

![image](https://user-images.githubusercontent.com/48269287/211867496-cb46e70d-1de4-4425-a7ca-4cd251a29230.png)

The basic structure of the driver program is listed above, depicted in three states.
* <b>STAGE_ONE</b>   : Acts as the main panel, where the user can input the link to the recipe
* <b>STAGE_TWO</b>   : Acts as a configuration, where the user can login or register, with also a built in database configurator
* <b>STAGE_THREE</b> : Acts as a user portal, where the user can retrieve saved recipes and search through them

A more complete flowchart of the program flow can be found below:

![image](https://user-images.githubusercontent.com/48269287/211869491-80136a9e-7531-4a23-a0af-19b1f89e546c.png)
