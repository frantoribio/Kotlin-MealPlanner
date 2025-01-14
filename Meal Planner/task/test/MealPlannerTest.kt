import org.hyperskill.hstest.dynamic.DynamicTest
import org.hyperskill.hstest.stage.StageTest
import org.hyperskill.hstest.testcase.CheckResult
import org.hyperskill.hstest.testing.TestedProgram
import java.io.File
import java.sql.DatabaseMetaData
import java.sql.DriverManager

data class MyMealTestData(val mealCategory: String, val mealName: String, val ingredients: List<String>)

val days = arrayOf("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday")
val mealsList = mutableListOf<MyMealTestData>(
    MyMealTestData("breakfast", "scrambled eggs", listOf("eggs", "milk", "cheese")),
    MyMealTestData("breakfast", "sandwich", listOf("bread", "cheese", "ham")),
    MyMealTestData("breakfast", "oatmeal", listOf("oats", "milk", "banana", "peanut butter")),
    MyMealTestData("breakfast", "english breakfast", listOf("eggs", "sausages", "bacon", "tomatoes", "bread")),
    MyMealTestData("lunch", "sushi", listOf("salmon", "rice", "avocado")),
    MyMealTestData("lunch", "chicken salad", listOf("chicken", "lettuce", "tomato", "olives")),
    MyMealTestData("lunch", "omelette", listOf("eggs", "milk", "cheese")),
    MyMealTestData("lunch", "salad", listOf("lettuce", "tomato", "onion", "cheese", "olives")),
    MyMealTestData("dinner", "pumpkin soup", listOf("pumpkin", "coconut milk", "curry", "carrots")),
    MyMealTestData("dinner", "beef steak", listOf("beef steak")),
    MyMealTestData("dinner", "pizza", listOf("flour", "tomato", "cheese", "salami")),
    MyMealTestData("dinner", "tomato soup", listOf("tomato", "orzo"))
)

class MealPlannerTest : StageTest<Any>() {

    @DynamicTest(order = 1)
    fun normalExe12Test(): CheckResult {
        try {
            val dbFile = File("meals.db")
            if (dbFile.exists()) dbFile.delete()
        } catch (e: Exception) {
            return CheckResult(false, "An exception was thrown, while trying to delete a database file.")
        }

        val co = CheckOutput()
        if (!co.start("What would you like to do (add, show, plan, exit)?"))
            return CheckResult(
                false,
                "Your program should ask the user about the required action: \"(add, show, plan, exit)?\""
            )

        val dbUrl = "jdbc:sqlite:meals.db"
        val tables = listOf(
            dbTable(
                "ingredients",
                listOf(Pair("ingredient", "text"), Pair("ingredient_id", "integer"), Pair("meal_id", "integer"))
            ),
            dbTable("meals", listOf(Pair("category", "text"), Pair("meal", "text"), Pair("meal_id", "integer")))
        )

        val (res1, errorStr1) = checkTableSchema(dbUrl, tables)
        if (!res1) return CheckResult(false, errorStr1)

        val (res2, errorStr2) = tableExists(dbUrl, "plan")
        if (!res2) return CheckResult(false, errorStr2)

        if (!co.input("exit", "Bye!"))
            return CheckResult(false, "Your output should contain \"Bye!\"")

        if (!co.programIsFinished())
            return CheckResult(false, "The application didn't exit.")

        return CheckResult.correct()
    }

    @DynamicTest(order = 2)
    fun normalExe13Test(): CheckResult {
        try {
            val dbFile = File("meals.db")
            if (dbFile.exists()) dbFile.delete()
        } catch (e: Exception) {
            return CheckResult(false, "An exception was thrown, while trying to delete a database file.")
        }

        try {
            val co = CheckOutput()
            if (!co.start("What would you like to do (add, show, plan, exit)?"))
                return CheckResult(
                    false,
                    "Your program should ask the user about the required action: \"(add, show, plan, exit)?\""
                )

            if (!co.input("add", "Which meal do you want to add (breakfast, lunch, dinner)?"))
                return CheckResult(
                    false,
                    "Your program should ask the user about meal category: \"(breakfast, lunch, dinner)?\""
                )

            if (!co.input("lunch", "Input the meal's name:"))
                return CheckResult(false, "Your output should contain \"Input the meal's name:\"")

            if (!co.input("sushi", "Input the ingredients:"))
                return CheckResult(false, "Your output should contain \"Input the ingredients:\"")

            if (!co.input("salmon, rice, avocado", "The meal has been added!"))
                return CheckResult(false, "Your output should contain \"The meal has been added!\"")

            if (!co.inputNext("What would you like to do (add, show, plan, exit)?"))
                return CheckResult(
                    false,
                    "Your program should ask the user about the required action: \"(add, show, plan, exit)?\""
                )

            if (!co.input("add", "Which meal do you want to add (breakfast, lunch, dinner)?"))
                return CheckResult(
                    false,
                    "Your program should ask the user about meal category: \"(breakfast, lunch, dinner)?\""
                )

            if (!co.input("lunch", "Input the meal's name:"))
                return CheckResult(false, "Your output should contain \"Input the meal's name:\"")

            if (!co.input("omelette", "Input the ingredients:"))
                return CheckResult(false, "Your output should contain \"Input the ingredients:\"")

            if (!co.input("eggs, milk, cheese", "The meal has been added!"))
                return CheckResult(false, "Your output should contain \"The meal has been added!\"")

            if (!co.inputNext("What would you like to do (add, show, plan, exit)?"))
                return CheckResult(
                    false,
                    "Your program should ask the user about the required action: \"(add, show, plan, exit)?\""
                )

            if (!co.input("add", "Which meal do you want to add (breakfast, lunch, dinner)?"))
                return CheckResult(
                    false,
                    "Your program should ask the user about meal category: \"(breakfast, lunch, dinner)?\""
                )

            if (!co.input("breakfast", "Input the meal's name:"))
                return CheckResult(false, "Your output should contain \"Input the meal's name:\"")

            if (!co.input("oatmeal", "Input the ingredients:"))
                return CheckResult(false, "Your output should contain \"Input the ingredients:\"")

            if (!co.input("oats, milk, banana, peanut butter", "The meal has been added!"))
                return CheckResult(false, "Your output should contain \"The meal has been added!\"")

            if (!co.inputNext("What would you like to do (add, show, plan, exit)?"))
                return CheckResult(
                    false,
                    "Your program should ask the user about the required action: \"(add, show, plan, exit)?\""
                )

            if (!co.input("show", "Which category do you want to print (breakfast, lunch, dinner)?"))
                return CheckResult(
                    false,
                    "Your program should ask the user about the meal category to print: \"(breakfast, lunch, dinner)?\""
                )

            if (!co.input(
                    "lunch", "Category: lunch", "Name: sushi", "Ingredients:", "salmon", "rice", "avocado",
                    "Name: omelette", "Ingredients:", "eggs", "milk", "cheese"
                )
            )
                return CheckResult(false, "Wrong \"show\" output for a saved meal.")

            if (!co.inputNext("What would you like to do (add, show, plan, exit)?"))
                return CheckResult(
                    false,
                    "Your program should ask the user about the required action: \"(add, show, plan, exit)?\""
                )

            if (!co.input("show", "Which category do you want to print (breakfast, lunch, dinner)?"))
                return CheckResult(
                    false,
                    "Your program should ask the user about the meal category to print: \"(breakfast, lunch, dinner)?\""
                )

            if (!co.input(
                    "breakfast", "Category: breakfast", "Name: oatmeal", "Ingredients:", "oats",
                    "milk", "banana", "peanut butter"
                )
            )
                return CheckResult(false, "Wrong \"show\" output for a saved meal.")

            if (!co.inputNext("What would you like to do (add, show, plan, exit)?"))
                return CheckResult(
                    false,
                    "Your program should ask the user about the required action: \"(add, show, plan, exit)?\""
                )

            if (!co.input("exit", "Bye!"))
                return CheckResult(false, "Your output should contain \"Bye!\"")

            if (!co.programIsFinished())
                return CheckResult(false, "The application didn't exit.")
        } catch (e: Exception) {
            return CheckResult(false, "An exception was thrown while testing - ${e.message}")
        }

        return CheckResult.correct()
    }

    @DynamicTest(order = 3)
    fun normalExe14Test(): CheckResult {
        try {
            val dbFile = File("meals.db")
            if (!dbFile.exists()) return CheckResult(false, "The meals.db database file doesn't exist.")
        } catch (e: Exception) {
            return CheckResult(false, "An exception was thrown, while trying to check a database file.")
        }
        try {
            val co = CheckOutput()
            if (!co.start("What would you like to do (add, show, plan, exit)?"))
                return CheckResult(
                    false,
                    "Your program should ask the user about the required action: \"(add, show, plan, exit)?\""
                )

            if (!co.input("show", "Which category do you want to print (breakfast, lunch, dinner)?"))
                return CheckResult(
                    false,
                    "Your program should ask the user about the meal category to print: \"(breakfast, lunch, dinner)?\""
                )

            if (!co.input(
                    "lunch", "Category: lunch", "Name: sushi", "Ingredients:", "salmon", "rice", "avocado",
                    "Name: omelette", "Ingredients:", "eggs", "milk", "cheese"
                )
            )
                return CheckResult(false, "Wrong \"show\" output for a saved meal.")

            if (!co.inputNext("What would you like to do (add, show, plan, exit)?"))
                return CheckResult(
                    false,
                    "Your program should ask the user about the required action: \"(add, show, plan, exit)?\""
                )

            if (!co.input("show", "Which category do you want to print (breakfast, lunch, dinner)?"))
                return CheckResult(
                    false,
                    "Your program should ask the user about the meal category to print: \"(breakfast, lunch, dinner)?\""
                )

            if (!co.input(
                    "breakfast", "Category: breakfast", "Name: oatmeal", "Ingredients:", "oats",
                    "milk", "banana", "peanut butter"
                )
            )
                return CheckResult(false, "Wrong \"show\" output for a saved meal.")

            if (!co.inputNext("What would you like to do (add, show, plan, exit)?"))
                return CheckResult(
                    false,
                    "Your program should ask the user about the required action: \"(add, show, plan, exit)?\""
                )

            if (!co.input("show", "Which category do you want to print (breakfast, lunch, dinner)?"))
                return CheckResult(
                    false,
                    "Your program should ask the user about the meal category to print: \"(breakfast, lunch, dinner)?\""
                )

            if (!co.input("brunch", "Wrong meal category! Choose from: breakfast, lunch, dinner."))
                return CheckResult(false, "Wrong output after the input of a category that doesn't exist.")

            if (!co.input("dinner", "No meals found."))
                return CheckResult(false, "Wrong output for a category with no added meals.")

            if (!co.input("exit", "Bye!"))
                return CheckResult(false, "Your output should contain \"Bye!\"")

            if (!co.programIsFinished())
                return CheckResult(false, "The application didn't exit.")
        } catch (e: Exception) {
            return CheckResult(false, "An exception was thrown while testing - ${e.message}")
        }

        return CheckResult.correct()
    }

    @DynamicTest(order = 4)
    fun normalExe15Test(): CheckResult {
        try {
            val dbFile = File("meals.db")
            if (dbFile.exists()) dbFile.delete()
        } catch (e: Exception) {
            return CheckResult(false, "An exception was thrown, while trying to delete a database file.")
        }

        try {
            val co = CheckOutput()
            if (!co.start("What would you like to do (add, show, plan, exit)?"))
                return CheckResult(
                    false,
                    "Your program should ask the user about the required action: \"(add, show, plan, exit)?\""
                )


            for (meal in mealsList) {
                if (!co.input("add", "Which meal do you want to add (breakfast, lunch, dinner)?"))
                    return CheckResult(
                        false,
                        "Your program should ask the user about meal category: \"(breakfast, lunch, dinner)?\""
                    )

                if (!co.input(meal.mealCategory, "Input the meal's name:"))
                    return CheckResult(false, "Your output should contain \"Input the meal's name:\"")

                if (!co.input(meal.mealName, "Input the ingredients:"))
                    return CheckResult(false, "Your output should contain \"Input the ingredients:\"")

                if (!co.input(meal.ingredients.joinToString(","), "The meal has been added!"))
                    return CheckResult(false, "Your output should contain \"The meal has been added!\"")

                if (!co.inputNext("What would you like to do (add, show, plan, exit)?"))
                    return CheckResult(
                        false,
                        "Your program should ask the user about the required action: \"(add, show, plan, exit)?\""
                    )

            }

            co.getNextOutput("plan")

            for ((index, day) in days.withIndex()) {
                if (!co.inputNext(day))
                    return CheckResult(false, "Your output should contain \"$day\"")

                for (category in listOf("breakfast", "lunch", "dinner")) {
                    if (!co.inputNext(
                            *mealsList.filter { it.mealCategory == category }.map { it.mealName }.sorted()
                                .toTypedArray()
                        )
                    )
                        return CheckResult(false, "Your output should contain the breakfast meals in alphabetic order.")

                    if (!co.inputNext("Choose the $category for $day from the list above:"))
                        return CheckResult(false, "Your output should contain the prompt for the $category meal.")

                    if (!co.input("nonExistMeal", "This meal doesn’t exist. Choose a meal from the list above."))
                        return CheckResult(
                            false,
                            "Your output should contain \"This meal doesn’t exist. Choose a meal from the list above.\""
                        )

                    co.getNextOutput(mealsList.filter { it.mealCategory == category }[index % 4].mealName)
                }
                if (!co.inputNext("Yeah! We planned the meals for $day."))
                    return CheckResult(false, "Your output should contain \"Yeah! We planned the meals for $day.\".")
            }

            val planPrintout = listOf(
                "Monday", "Breakfast: scrambled eggs", "Lunch: sushi", "Dinner: pumpkin soup",
                "Tuesday", "Breakfast: sandwich", "Lunch: chicken salad", "Dinner: beef steak",
                "Wednesday", "Breakfast: oatmeal", "Lunch: omelette", "Dinner: pizza",
                "Thursday", "Breakfast: english breakfast", "Lunch: salad", "Dinner: tomato soup",
                "Friday", "Breakfast: scrambled eggs", "Lunch: sushi", "Dinner: pumpkin soup",
                "Saturday", "Breakfast: sandwich", "Lunch: chicken salad", "Dinner: beef steak",
                "Sunday", "Breakfast: oatmeal", "Lunch: omelette", "Dinner: pizza"
            )

            for (line in planPrintout) {
                if (!co.inputNext(line))
                    return CheckResult(false, "Your output should contain \"$line\".")
            }

            if (!co.input("exit", "Bye!"))
                return CheckResult(false, "Your output should contain \"Bye!\"")

            if (!co.programIsFinished())
                return CheckResult(false, "The application didn't exit.")

        } catch (e: Exception) {
            return CheckResult(false, "An exception was thrown while testing - ${e.message}")
        }

        return CheckResult.correct()
    }

}

class dbTable(val name: String, val columnNameType: List<Pair<String, String>>)

fun tableExists(dbUrl: String, tableName: String): Pair<Boolean, String> {
    try {
        val connection = DriverManager.getConnection(dbUrl)
        val meta: DatabaseMetaData = connection.metaData

        val tableMeta = meta.getTables(null, null, tableName, null)
        if (!tableMeta.next() || tableName.lowercase() != tableMeta.getString("TABLE_NAME").lowercase())
            return Pair(false, "The table \"${tableName}\" doesn't exist.")
        println(tableName)
        println(tableMeta.getString("TABLE_NAME").lowercase())

        connection.close()
    } catch (e: Exception) {
        return Pair(
            false,
            "An exception was thrown, while trying to check if a table exists in the database - ${e.message}"
        )
    }

    return Pair(true, "")
}

fun checkTableSchema(dbUrl: String, tables: List<dbTable>): Pair<Boolean, String> {
    try {
        val connection = DriverManager.getConnection(dbUrl)
        val meta: DatabaseMetaData = connection.metaData

        for (table in tables) {
            var tableMeta = meta.getTables(null, null, table.name, null)
            if (tableMeta.next() && table.name != tableMeta.getString("TABLE_NAME").lowercase())
                return Pair(false, "The table \"${table.name}\" doesn't exist.")

            var columns = meta.getColumns(null, null, table.name, null)
            val columnsData = mutableListOf<Pair<String, String>>()
            while (columns.next()) {
                val column = Pair(
                    columns.getString("COLUMN_NAME").lowercase(),
                    columns.getString("TYPE_NAME").lowercase()
                )
                columnsData.add(column)
            }

            for (c in table.columnNameType) {
                if (c !in columnsData) {
                    if (c.first !in columnsData.map { it.first })
                        return Pair(false, "The column \"${c.first}\" of the table \"${table.name}\" doesn't exist.")
                    return Pair(false, "The column \"${c.first}\" of the table \"${table.name}\" is of the wrong type.")
                }
            }
        }

        connection.close()
    } catch (e: Exception) {
        return Pair(false, "An exception was thrown, while trying to check the database schema - ${e.message}")
    }

    return Pair(true, "")
}

class CheckOutput {
    private var main: TestedProgram = TestedProgram()
    var position = 0
    private var caseInsensitive = true
    private var trimOutput = true
    private val arguments= mutableListOf<String>()
    private var isStarted = false
    private var lastOutput = ""

    private fun checkOutput(outputString: String, vararg checkStr: String): Boolean {
        var searchPosition = position
        for (cStr in checkStr) {
            val str = if (caseInsensitive) cStr.lowercase() else cStr
            val findPosition = outputString.indexOf(str, searchPosition)
            if (findPosition == -1) return false
            if ( outputString.substring(searchPosition until findPosition).isNotBlank() ) return false
            searchPosition = findPosition + str.length
        }
        position = searchPosition
        return true
    }

    fun start(vararg checkStr: String): Boolean {
        return if (!isStarted) {
            var outputString = main.start(*arguments.toTypedArray())
            lastOutput = outputString
            if (trimOutput) outputString = outputString.trim()
            if (caseInsensitive) outputString = outputString.lowercase()
            isStarted = true
            checkOutput(outputString, *checkStr)
        } else false
    }

    fun stop() {
        main.stop()
    }

    fun input(input: String, vararg checkStr: String): Boolean {
        if (main.isFinished) return false
        var outputString = main.execute(input)
        lastOutput = outputString
        if (trimOutput) outputString = outputString.trim()
        if (caseInsensitive) outputString = outputString.lowercase()
        position = 0
        return checkOutput(outputString, *checkStr)
    }

    fun inputNext(vararg checkStr: String): Boolean {
        var outputString = lastOutput
        if (trimOutput) outputString = outputString.trim()
        if (caseInsensitive) outputString = outputString.lowercase()
        return checkOutput(outputString, *checkStr)
    }

    fun getNextOutput(input: String): String {
        if (main.isFinished) return ""
        val outputString = main.execute(input)
        lastOutput = outputString
        position = 0
        return  outputString
    }

    fun getLastOutput(): String { return lastOutput }
    fun programIsFinished(): Boolean  = main.isFinished
    fun setArguments(vararg args: String) { arguments.addAll(args.toMutableList()) }
    fun setCaseSensitivity(caseInsensitive: Boolean) { this.caseInsensitive = caseInsensitive }
    fun setOutputTrim(trimOutput: Boolean) { this.trimOutput = trimOutput}
}


