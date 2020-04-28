package com.example.userr.rts_lab32_android;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;

import static java.lang.Math.abs;

class Equation{
    int a,b,c,d,y;
    Equation(int _a, int _b, int _c, int _d, int _y){
        a=_a; b=_b; c=_c; d=_d; y=_y;
    }
    int fitness_function(int[] xArr){
        return abs(a*xArr[0]+b*xArr[1]+c*xArr[2]+d*xArr[3] - y); }
    int fitness_function(int x1, int x2, int x3, int x4){
        return  fitness_function(new int[]{x1,x2,x3,x4}); }
}
class Lab3_3 {
    static void fill_array_randomly(int[][] arr, int rndMin, int rndMax) {
        Random rnd = new Random();
        for (int i = 0; i < arr.length; ++i)
            for (int j = 0; j < arr[0].length; ++j)
                arr[i][j] = rnd.nextInt(rndMax - rndMin) + rndMin;
    }

    static void fill_array_randomly(int[][] arr) {
        fill_array_randomly(arr, 0, 10);
    }

    //n-розмір популяції, rndMin,rndMax -границі генерації випадкових чисел для першої популяції
    static String array_to_string(int[][] arr) {
        String text = "";
        for (int[] arr1d : arr)
            text += Arrays.toString(arr1d) + "\n";
        return text;
    }

    static int[] crossing(int[] parent1, int[] parent2) { //схрещування
        int n = parent1.length, i;
        int[] progeny = new int[n]; //потомство
        Random rnd = new Random();
        for (i = 0; i < n; ++i) {
            if (rnd.nextInt(2) == 0) progeny[i] = parent1[i];
            else progeny[i] = parent2[i];
        }
        return progeny;
    }

    static void genetic_algorithm(Equation equation, int n, TextView textView) {
        int[][] population = new int[n][4];
        int[][] parents = new int[n][4]; //батьки, що сформують нову популяцію
        int[][] newPopulation = new int[n][4];

        float[] reversedFitnessArr = new float[n];
        float reversedFitnessArrSum;
        float[] probabilityArr = new float[n];
        int i, j, fitnessValue, gen = 1;
        Random random = new Random();
        int rnd1, rnd2, rnd3; //випадкові числа
        float randomValue;
        boolean calculationComplete = false;
        //Scanner scanner = new Scanner(System.in);

        fill_array_randomly(population);
        textView.append(equation.a+"*x1+"+equation.b+"*x2+"+equation.c+"*x3+"+equation.d+"*x4="+equation.y+"\n");

        while (true) {
            textView.append("покоління " + gen++ +"\n");
            textView.append("популяція:\n" + array_to_string(population)+"\n");

            reversedFitnessArrSum = 0.f;
            for (i = 0; i < n; ++i) {
                fitnessValue = equation.fitness_function(population[i]);
                if (fitnessValue == 0) {
                    calculationComplete = true;
                    break;
                }
                reversedFitnessArr[i] = 1.f / fitnessValue;
                reversedFitnessArrSum += reversedFitnessArr[i];
            }
            if (calculationComplete) {
                textView.append("Обчислення завершено! Підходящий генотип:\n" + Arrays.toString(population[i])+"\n");
                break;
            }

            textView.append("Значення ф-цї пристосованості: " + Arrays.toString(reversedFitnessArr) + "    сума: " + reversedFitnessArrSum+"\n");

            for (i = 0; i < n; ++i) //формування ймовірностей для кожної особи
                probabilityArr[i] = reversedFitnessArr[i] / reversedFitnessArrSum;
            textView.append("ймовірності: " + Arrays.toString(probabilityArr)+"\n");

            //формування масиву сум ймовірностей, значення зростають, останне значення = 1
            for (i = 1; i < n; ++i)
                probabilityArr[i] += probabilityArr[i - 1];
            textView.append("суми ймовірностей: " + Arrays.toString(probabilityArr)+"\n");

            for (i = 0; i < n; ++i) { //i-номер батька
                randomValue = random.nextFloat();
                for (j = 0; j < n; ++j)//знаходження батька випадково враховуючи вірогідності
                    if (randomValue < probabilityArr[j]) {
                        parents[i] = population[j];
                        break;
                    }
            }
            textView.append("батьки:\n" + array_to_string(parents)+"\n");

            for (i = 0; i < n; i += 2) {//схрещювання
                newPopulation[i] = crossing(parents[i], parents[i + 1]);
                newPopulation[i + 1] = crossing(parents[i], parents[i + 1]);
            }
            population = newPopulation;

            for (i = 0; i < 3; ++i) { //мутація
                rnd1 = random.nextInt(n);
                rnd2 = random.nextInt(4);
                rnd3 = random.nextInt(5) - 2;
                population[rnd1][rnd2] += rnd3;
            }
            textView.append("--------------------------------------------------\n");
            //scanner.next();
        }
    }
}
public class Lab33Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lab33);

        final EditText equationCoeffEditText = (EditText)findViewById(R.id.equationCoeffEditText);
        final EditText populationSizeEditText = (EditText)findViewById(R.id.populationSizeEditText);
        Button geneticAlgorithmButton = (Button)findViewById(R.id.geneticAlgorithmButton);
        final TextView geneticAlgorithmTextView = (TextView) findViewById(R.id.geneticAlgorithmTextView);
        geneticAlgorithmTextView.setMovementMethod(new ScrollingMovementMethod());
        geneticAlgorithmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                geneticAlgorithmTextView.setText("");
                String[] coeffStr = equationCoeffEditText.getText().toString().split(" ");
                int a,b,c,d,y;
                a=Integer.parseInt(coeffStr[0]);
                b=Integer.parseInt(coeffStr[1]);
                c=Integer.parseInt(coeffStr[2]);
                d=Integer.parseInt(coeffStr[3]);
                y=Integer.parseInt(coeffStr[4]);
                Equation equation = new Equation(a,b,c,d,y);

                int n = Integer.parseInt(populationSizeEditText.getText().toString());
                Lab3_3.genetic_algorithm(equation,n,geneticAlgorithmTextView);
            }
        });

    }
}
