# Database Query Anlyser:

The code structure consists of Hashmap to map table_name to column_name_list

HashMap<String, Column> hmap	
table_name	 HashMap<String,Integer> column_name_list

* This is for Optimization no. 1, which is to merge multiple select statements into a single select
Another Hashmap to map column_name to its count

HashMap<String,Integer> column_name_list	
Column_name	count

This is for Optimization no. 2, which is to eliminate the unused columns
All other operations are parsing the file and performing string match 
Combining multiple select statements into a single Select statement. 

Description:
 - Accessing the Database multiple times for the same operation should be avoided
 - Query Processing for each access consumes significant time

Implementation Detail:
 - Create a hashmap for table_name associated with hashmap of columns associated with their reference count.
 - Combined multiple selects from same table into single select query

Output:

* Picking only needed columns:
Description:
- The code might fetch data that was never unused, detecting that and eliminating it. 

Implementation Detail:
- Keep a counter for the mention of column name in the code.
- If the column data was never used, give the suggestion to eliminate it from the query

* Looping Queries: Looping Select queries can be optimized by forming multiple JOIN instead 

Description:
- Select queries can be optimized by forming multiple JOIN instead of nested select.
Eg: select name from yourtable where age in (select age from kids)
This query will select all the rows from kids whether or not the age matches the one in yourtable. Instead if we perform join on age column , only the required rows will be fetched.

Implementation Detail:
Suggest that join can be performed instead of nested select loop.

 Optimizing queries to retrieve data in indexed manner
Found that it should be on database side. Index needs to be created on the database table. Thus our optimizer suggests the developer to do so.
------------------------

Challenges and Time Division:
Needed to Revise SQL concepts and learn query optimization – 5 hrs
Learned Java Script basics – 5 hrs
Code implementation – 10 hrs
Debugging and refining – 14 hrs
Documentation – 2 hrs
I was  a C developer for 4 yrs, hence developing in Java took some time. Learned and applied hashmap, regex operations for optimizations.  Most of the code can be used for generic database optimization suggestions.  The suggestions are appended into the output file wherever necessary in the form of comments so as to directly point the code that can be optimized.
