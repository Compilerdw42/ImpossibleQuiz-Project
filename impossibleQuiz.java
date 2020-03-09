/* *****************************************
 AUTHOR: Denis Grigoryev
 DATE: 10.12.2019
 Mini-project - "Impossible Quiz"
 This program asks the player a question,
 after the player's response it prints out the correct answer and gives points for right answers
 ***************************************** */
import java.util.*;
import java.io.*;

class impossibleQuiz
{
    public static void main(String [] param) throws Exception
    {
        int sum = 0;//all the points, accumulator variable

        String sourceIntro = "intro.txt";
        String[] lines = new String[7];

        //print out the welcoming paragraph
        BufferedReader inputStream = new BufferedReader(new FileReader(sourceIntro));

        for(int i = 0; i<lines.length; i++)
        {
            lines[i] = inputStream.readLine();
            System.out.println(lines[i]);
        }
        inputStream.close();
        System.out.println();

        sum = first();//main action happens here

        //to write to a file////////////////////////////////////////////
        String sumToPrint = Integer.toString(sum);//convert into String to print

        PrintWriter outputStream = new PrintWriter(new FileWriter("mydata.txt"));
        outputStream.println("Your score " + sumToPrint);
        outputStream.close();

        //to read from the file
        BufferedReader inputStream2 = new BufferedReader(new FileReader("mydata.txt"));
        String s = inputStream2.readLine(); 
        //the print out s
        System.out.println(s);
        inputStream2.close();

        ////////////////////////////////////////////////////////////////
        System.exit(0);
    }//END main

    //prints out the message, reads the input, prints the correct answer
    public static int first()
    {
        //return score
        int score = 0;
        boolean yesorno;
        boolean impossible = false;
        int numberOfQuestions = 5;

        //create questions***********************************
        Questions q0 = createNewQuest("Which of the following elements is a metal:" + "\n" + "Copper, Trash or Carbon?", "Copper", "Trash", "Carbon");
        Questions q1 = createNewQuest("What is Denis' favourite drink: \nTea, Coffee or Milk?", "Tea", "Coffee", "Milk"); 
        Questions q2 = createNewQuest("What is Denis' favourite city: \nLondon, Brussels or Amsterdam?", "London", "Brussels", "Amsterdam");
        Questions q3 = createNewQuest("What animal is native to New Zealand: \nKangaroo, Wombat or Kiwi?", "Kiwi", "Kangaroo", "Wombat"); 
        Questions q4 = createNewQuest("How tall was Lord Horatio Nelson: \nA. 6ft 2in, B. 5ft 4in or C. 5ft", "B", "A", "C");

        Questions q = createNewQuest ("", "", "", "");
        //********************************************

        //Abstract data type here
        //create empty Questionbank (Queue)*******
        QuestionBank qb = createEmptyQB(numberOfQuestions); 

        //Fill the Questionbank with questions (join the queue) 
        qb = joinQB(qb, q0);
        qb = joinQB(qb, q1);
        qb = joinQB(qb, q2);
        qb = joinQB(qb, q3);
        qb = joinQB(qb, q4);
        //****************************************

        int whatToDo = 0;//decides what to do next

        while(!(whatToDo==1))
        {
            int dicethrow = getRandom(numberOfQuestions); 
            q = chooseQuest(qb, dicethrow, q); 

            while (q.used == true)
            {
                dicethrow = getRandom(numberOfQuestions);
                q = chooseQuest(qb, dicethrow, q);// to see what questions have already been used
            }
            q.used = true;

            //get the answer from the user and act
            String answer1 = inputString(getQuest(q));
            yesorno = evaluate(answer1, getCorrect(q));
            impossible = impossibleCheck(answer1, q);
            
            while (impossible == true)
            {
                print("Ooops, that's wrong! \nThe right answer is " + getCorrect(q) + " and your answer is IMPOSSIBLE!!!");
                print("\nYou lose all the points ;) \n");
                score = 0;
                answer1 = inputString(getQuest(q));
                impossible = impossibleCheck(answer1, q);
                yesorno = evaluate(answer1, getCorrect(q));
            }
            if(yesorno)
            {
                print("You got it right!");
                int gm = givemarks();
                score = score + gm; //score = score + givemarks();
                printInt(score);

                qb = setPoints(qb, gm, dicethrow);
            }
            else
            {
                print("Ooops, that's wrong! \nThe right answer is " + getCorrect(q)); 
                printInt(score);
                qb = setPoints(qb, 0, dicethrow);
            }

            whatToDo = inputInt("Do you want to quit?\n1.YES\n2.NO\n3.PRINT a table");
            if(whatToDo == 3)
            {
                //BUBBLESORT
                for(int pass = 1; pass<=qb.points.length -1; pass++)
                {
                    for(int i = 0; i<(qb.points.length - pass); i++)
                    {
                        if(qb.points[i]>qb.points[i+1])
                        {
                            swap(qb.points, qb.questionbank, i, i+1);
                        }
                    }
                }
                printTable(qb, numberOfQuestions);
                return score;
            }
        }
        return score;
    }

    //to get a random question
    public static Questions chooseQuest(QuestionBank qb, int dicethrow, Questions q)//could be improved by passing just an array
    {
        //int dicethrow = getRandom(numberOfQuestions); 
        if(dicethrow == 0)
            q = qb.questionbank[0];
        else if(dicethrow == 1)
            q = qb.questionbank[1];
        else if (dicethrow == 2)
            q = qb.questionbank[2];
        else if (dicethrow == 3)
            q = qb.questionbank[3];
        else
            q = qb.questionbank[4];

        return q;
    }

    //to swap two places
    public static void swap(int[] points, Questions[] questionbank, int i , int a)
    {
        int tmp = points[i];
        points[i] = points[a];
        points[a] = tmp;

        Questions temp = createNewQuest("", "", "", "");
        temp = questionbank[i];
        questionbank[i] = questionbank[a];
        questionbank[a] = temp;
    }

    //to print the table
    public static void printTable(QuestionBank qb, int howmany)
    {
        for(int i = 0; i < howmany; i++)
        {
            Questions qtemp = createNewQuest("","","","");
            qtemp = qb.questionbank[i]; 

            System.out.print("Question " + (i + 1) +" -> \n");
            System.out.println(getQuest(qtemp));
            System.out.print("Answer-> ");
            System.out.print(getCorrect(qtemp));
            System.out.print("     Points-> ");
            System.out.print(qb.points[i]);
            System.out.println();
            print("------------------------------------------------");
        }
    }
    //returns a random int between 0 and a
    public static int getRandom(int a)
    {
        Random dice = new Random();
        int randomInteger = dice.nextInt(a);
        return randomInteger;
    }
    //evaluates whether or not the answer is correct
    public static boolean evaluate(String userinput, String correctAnswer)
    {
        if (userinput.equals(correctAnswer))
            return true;
        else
            return false;
    }

    // checks whether the answer was not only wrong but impossible
    public static boolean impossibleCheck(String userinput, Questions q)
    {
        if (userinput.equals(getWrong1(q)) || userinput.equals(getWrong2(q)) || userinput.equals(getCorrect(q)))
            return false;
        else
            return true;
    }

    // throws a dice to decide what marks to give
    public static int givemarks()
    {
        Random dice = new Random();
        int dicethrow = dice.nextInt(10) + 1;
        return dicethrow;
    }

    // gets user input as a String
    public static String inputString(String message)
    {
        Scanner scanner = new Scanner(System.in);
        print(message);
        String ans = scanner.nextLine();
        return ans;
    }

    // gets user input as an integer
    public static int inputInt(String message)
    {
        Scanner scanner = new Scanner(System.in);
        print(message);
        int ans1 = Integer.parseInt(scanner.nextLine());
        return ans1;
    }

    // prints the passed value
    public static void print(String message)
    {
        System.out.println(message);
    }
    public static void printInt(int number)
    {
        System.out.println(number);
    }

    // GETTER METHODS **********************************************
    public static String getQuest(Questions q)
    {
        return q.question;
    }
    public static String getCorrect(Questions q)
    {
        return q.correctAns;
    }
    public static String getWrong1(Questions q)
    {
        return q.wrongAns1;
    }
    public static String getWrong2(Questions q)
    {
        return q.wrongAns2;
    }
    //**************************************************************
    //SETTER METHODS ***********************************************
    public static Questions createNewQuest(String que, String cA, String W1, String W2)
    {
        Questions q = new Questions();
        q.question = que;
        q.correctAns = cA;
        q.wrongAns1 = W1;
        q.wrongAns2 = W2;
        q.used = false;
        return q;
    }

    public static QuestionBank setPoints(QuestionBank qb, int score, int currentQuestion)
    {
        qb.points[currentQuestion] = score;
        return qb;
    }
    //**************************************************************
    public static QuestionBank createEmptyQB(int numberOfQuestions)
    {
        QuestionBank qb = new QuestionBank();
        Questions a [] = new Questions[numberOfQuestions];

        qb.questionbank = a;
        qb.nextQuestion = 0;
        int[] array = new int[numberOfQuestions];
        qb.points = array;
        return qb;
    }
    //to join the question bank
    public static QuestionBank joinQB(QuestionBank qb, Questions q)
    {
        qb.questionbank[qb.nextQuestion] = q;
        qb.nextQuestion = qb.nextQuestion + 1;
        return qb;
    }
}//END impossibleQuiz

//class of questions
class Questions
{
    String question;
    String correctAns;
    String wrongAns1;
    String wrongAns2;
    boolean used;
}

//Operations allowed on the abstract data type
//1. create empty Question Bank
//2. join the question
class QuestionBank
{
    Questions[] questionbank;
    int nextQuestion;
    int[] points;
}