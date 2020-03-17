package org.insurance.methods;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;

public class InsuranceComputation {
	static HashMap<Integer, ArrayList<Double>> relativeImportance = new HashMap<Integer, ArrayList<Double>>();
	static HashMap<String, Integer> features = new HashMap<String, Integer>();
	static HashMap<String, Double> featureScoreMap = new HashMap<String, Double>();

	static HashMap<String, ArrayList<Integer>> alternativePolicy = new HashMap<String, ArrayList<Integer>>();
	static HashMap<String, ArrayList<Integer>> alternativePolicyDiff = new HashMap<String, ArrayList<Integer>>();
	static HashMap<String, ArrayList<Double>> grc = new HashMap<String, ArrayList<Double>>();

	static HashMap<Integer, ArrayList<Integer>> minmax = new HashMap<Integer, ArrayList<Integer>>();

	InsuranceComputation() {
		relativeImportance.put(5, new ArrayList<Double>(Arrays.asList(0.9, 0.05, 0.05)));
		relativeImportance.put(4, new ArrayList<Double>(Arrays.asList(0.75, 0.2, 0.05)));
		relativeImportance.put(3, new ArrayList<Double>(Arrays.asList(0.5, 0.4, 0.1)));
		relativeImportance.put(2, new ArrayList<Double>(Arrays.asList(0.25, 0.6, 0.15)));
		relativeImportance.put(1, new ArrayList<Double>(Arrays.asList(0.1, 0.8, 0.1)));

		features.put("Low Premium", 5);
		features.put("Flexibility in Payment Structure", 2);
		features.put("Tax Benifits in Insurance Plan", 3);

		features.put("Benifits on Death", 5);
		features.put("Benifits on Survival", 4);
		features.put("Good Customer Service", 2);

		features.put("Bonus", 4);
		features.put("Add-ons & Special Scheme", 1);
		features.put("Availability of riders enabling customization of insurance plan", 3);

		alternativePolicy.put("p1", new ArrayList<Integer>(Arrays.asList(100,0,60,66,64,80,80,60,100)));
		alternativePolicy.put("p2", new ArrayList<Integer>(Arrays.asList(80,100,80,85,90,80,100,60,0)));
		alternativePolicy.put("p4", new ArrayList<Integer>(Arrays.asList(72,0,86,86,88,80,90,80,0)));
		alternativePolicy.put("p6", new ArrayList<Integer>(Arrays.asList(92,75,64,70,74,80,90,0,100)));
		alternativePolicy.put("p7", new ArrayList<Integer>(Arrays.asList(86,50,70,74,80,80,100,0,100)));
	}

	float computeSumOfFeatureImportance(){
		double sum = 0.0;
		for(String feature : features.keySet()) {
			ArrayList<Double> values = relativeImportance.get(features.get(feature));
			double mu = values.get(0);
			double v = values.get(1);
			double pi = values.get(2);

			sum += (mu + pi*(mu / (mu + v)));
		}
		return (float)sum;
	}

	void computeAlternativePolicyDifference() {
		for(String policy: alternativePolicy.keySet()) {
			ArrayList<Integer> al = alternativePolicy.get(policy);
			Integer[] policy1 = new Integer[9];
			for(int i=0;i<al.size();i++) {
				policy1[i] = Math.abs(100-al.get(i));
			}
			ArrayList<Integer> array_list =  
					new ArrayList<Integer>(Arrays.asList(policy1));
//			System.out.println(array_list.toString());
			alternativePolicyDiff.put(policy, array_list);
		}
	}

	public int[][] transpose (int[][] array) {
		if (array == null || array.length == 0)//empty or unset array, nothing do to here
			return array;

		int width = array.length;
		int height = array[0].length;

		int[][] array_new = new int[height][width];

		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				array_new[y][x] = array[x][y];
			}
		}
		return array_new;
	}

	public static Integer[] toObject(int[] intArray) {

		Integer[] result = new Integer[intArray.length];
		for (int i = 0; i < intArray.length; i++) {
			result[i] = Integer.valueOf(intArray[i]);
		}
		return result;

	}

	void computeGreyRelationalCoefficient() {
		int[][] matrix = new int[5][9];
		int i1=0;
		for(String policy: alternativePolicyDiff.keySet()) {
			matrix[i1] = alternativePolicyDiff.get(policy).stream().mapToInt(i -> i).toArray();
			i1+=1;
		}
		int[][] trans = transpose(matrix);
		for(int i=0; i<9; i++) {
			Arrays.sort(trans[i]);
			ArrayList<Integer> array_list1 =  
					new ArrayList<Integer>(Arrays.asList(trans[i][0], trans[i][4]));
			minmax.put(i, array_list1);
		}
		for(String policy: alternativePolicyDiff.keySet()) {
			ArrayList<Integer> al = alternativePolicyDiff.get(policy);
			Double[] policy1 = new Double[9];
			for(int i=0;i<al.size();i++) {
				ArrayList<Integer> minmax1 = minmax.get(i);
				System.out.println(minmax1.toString());
				policy1[i] = (minmax1.get(0).doubleValue() + (0.1 * minmax1.get(1).doubleValue()))/(al.get(i).doubleValue() +(0.1 * minmax1.get(1).doubleValue()));
			}
			ArrayList<Double> array_list =  
					new ArrayList<Double>(Arrays.asList(policy1));
			System.out.println(array_list.toString());
			grc.put(policy, array_list);
		}
	}

	void computeGreyRelationalGrade() {
		for(String policy: grc.keySet()) {
			ArrayList<Double> al = grc.get(policy);
			double sum = 0.0;
			for(int i=0;i<al.size();i++) {
				sum+=al.get(i);
			}
			System.out.println(policy+" "+grc.get(policy).toString());
			System.out.println(policy+" "+(1.0/9.0)*sum);
		}
	}

	public static void main(String[] args) {
		float sum = new InsuranceComputation().computeSumOfFeatureImportance();
		for(String feature: features.keySet()) {
			ArrayList<Double> values = relativeImportance.get(features.get(feature));
			double mu = values.get(0);
			double v = values.get(1);
			double pi = values.get(2);

			double importance = ((mu + pi*(mu/(mu+v)))/sum);
			importance = (double) Math.round(importance*1000d)/1000d;
			featureScoreMap.put(feature, importance);
		}
		new InsuranceComputation().computeAlternativePolicyDifference();
		new InsuranceComputation().computeGreyRelationalCoefficient();
		new InsuranceComputation().computeGreyRelationalGrade();
	}
}
