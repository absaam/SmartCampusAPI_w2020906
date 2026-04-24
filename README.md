# SmartCampusAPI_w2020906
Smart Campus REST API

## Overview
This API  was created as part of the university's Smart Campus initiative. It allows the facilities managers of the university to manage the rooms and sensors that are present within the university buildings. The three main resources are Rooms, Sensors, and Sensor Readings. All of the API endpoints are available at the base path /api/v1.

## How to Build and Run 

**1.** download the repository from GitHub

**2.** Open the project in Apache NetBeans 

**3.** Make sure Apache tomcat is added in the services tab 

**4.** Right click the project and select Clean and Build 

**5.** Wait for BUILD SUCCESS in the output window 

**6.** Right-click the project and select Run 

**7.** The API wil be available at:http://localhost:8080/SmartCampusAPI_w2020906/api/v1

## Sample curl Commands 

**Get all rooms**

curl -X GET http://localhost:8080/SmartCampusAPI_w2020906/api/v1/rooms

**Get a specific room**

curl -X GET http://localhost:8080/SmartCampusAPI_w2020906/api/v1/rooms/WEST-101

**Get all sensors**

curl -X GET http://localhost:8080/SmartCampusAPI_w2020906/api/v1/sensors

**Get a specific sensor**

curl -X GET http://localhost:8080/SmartCampusAPI_w2020906/api/v1/sensors/CO2-W01

**(POST)Create a new room**
curl -X POST http://localhost:8080/SmartCampusAPI_w2020906/api/v1/rooms \
-H "Content-Type: application/json" \
-d '{"id":"LIB-301","name":"Library Study Room","capacity":20}'

##  Part 1 - Question 1
By defult, JAX-RS creats new resourse instance for each request instead of using a singleton. This means data inside the resource class is not shared between requests. To keep data in memory, i used a separate DataStore class with static HashMaps so it can be shared. The issue is that multiple threads can access it at the same time, and since HashMap is not thread-safe, this can cause race conditions, to avoid this using something like a concurrentHashMap or synchronisation would be better.

## Part 1 - Question 2 

HATEOAS means that instead of the client having to already know all the URLs, the API response includes links that tells the client where it can go next. So the API guides you through it. This is better than static documentation because the documentation can go out of date, but the links in the response are always up to date. it also means developers dont have to hardcore URLs into their applications. In my API, when you hit GET /api/v1 it returns links to /api/v1/rooms and /api/v1/sensors so you can find everything from that one endpoint.

### part 2 - Question 1 

Returning only room IDs makes the response smaller, so it uses less bandwidth.The downside is that the client may need to send extra requests to get full details for each room. 

Returning the full room objects gives the client all the information straight away, which is easier for the client, but the response will be larger.

For my API, I returned the full room objects because the data set is small, so the extra bandwidth is not a big issue, and it avoids making the client do more requests.

### Part 2 - Question 2 
Yes, delete is idempotent in my implementaion.The first time you send a DELETE request for a room, it gets removed from the system. If you send the exact same request again, the room is already gone so nothing changes. You will get a 404 Not Found response becasue the room does not exist anymore, but the system is still in the same state after the first delete. So  no matter how many times you send the same DELETE request, the end result is the same Room is gone.

### Part 3 - Question 1 

The @consumes (MediaType.APPLICATION_JSON) annotation means this enpoint only accepts JSON data. JAX-RS checks the requests Content - Type to see if it matches. If a client sends something like text/plain or application/xml. it doesnt match what the method is expecting. When that happens, JAX-RS just rejects the request before it even gets to  my code and returns a 415 Unsuppoted Media Type error. I think this is useful because it means i dont have to manually check the format myself. It also helps avoid mistakes, since only the correct data format will be accepted by the API.

### Part 3 - Question 2 
Query paramenters are better for filtering as they are flexible and correspond to how filtering actually. Works with @QueryParam you can write a singe method to handle GET /api/v1/sensors with or without filters. The client adds ?type=CO2 if they need it. If you use paths like /api/v1/sensors/type/CO2, you need different methods for filtered and unfiltered requests. Query parameters are easy to combine as well. You can do ?type=CO2&status=ACTIVE without any other code. The path based filtering results in unfriendly URLs like /sensors/type/CO2/status/ACTIVE. It only gets worse when you add more filters. REST paths: resource identificaion./api/v1/sensors is the list of sensors, making it /type/CO2 makes it look like a different resource not a filtered view. Query parameters allow you to narrow downn the results, while keeping the identity of the resource clear.

### Part 4 - Question 1 
The Sub Resource locater pattern contributes to making the code more structured by separating nested resources in separete classes instead of putting everything in one big control system. Without it, a class like SensorResource would need to take care of plenty of paths like /sensors/, sensors/{id}, /sensor/{id}/readings and /sensors/{id}/readings/{rid} which can be disorganised and hard to maintain. In the case of sub resource locators, the top level (sensors) are handled by the main resourse, which then assigns resposibility for the nested parts to another class for example SensorReadingResource so each class has a specific responsibilty, allwoing the code to be easily maintained, read and updated especially as the API expands.

### Part 5 - Question 1 


HHTP 422 is more appropriate here, because the request itself is OK, but the data it contains is ot. For example if the JSON is correct but refers to something that does not exist roomID for example, the server understands the request but cannot process it properly. 404 means that the endpoint or resource in the URL does not exist. bute here the URL is OK the problem is in the request body. So 422 makes more sense as it says the request was valid but usable becasuse of the data it contains

### Part 5 - Question 2 

Showing users a java stack trace is dangerous because it gives away too much information. Its like providing people the 'backstage' of how a system works. That information can be used to determine how your system is built allowing them to attempt to find ways to breach it, allowing them to acess file names, class names, bits of the code,and how different parts are connected allowing them a full understanding on how it was built.

### Part 5 - Question 
Just makes things a lot easier to use JAX-RS filters for logging. You can handle Logger.info() in one place rather than adding it to every method. If you try to do it manually everywhere the code gets repetitive and messy, and its to forget to add logging in some methods. With a filter, it just happens to every request automatilcally. it keeps the code cleaner and saves time and you know all your logs are consistent instead of being slightly different in each method.
