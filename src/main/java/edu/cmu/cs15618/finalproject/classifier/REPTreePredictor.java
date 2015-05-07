package edu.cmu.cs15618.finalproject.classifier;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import weka.core.Instance;
import weka.core.Instances;
import weka.classifiers.Classifier;
import weka.classifiers.trees.REPTree;

public class REPTreePredictor implements RequestNumPredictor {

	private Classifier myClassifier;
	private Instances dataSet;
	

	@Override
	public void initClassifier(String trainingSetPath) {
		try {
			BufferedReader reader = new BufferedReader(new FileReader(
					trainingSetPath));

			dataSet = new Instances(reader);
			reader.close();
			// setting class attribute
			dataSet.setClassIndex(dataSet.numAttributes() - 1);

			// myClassifier = new M5P();
			myClassifier = new REPTree();
			myClassifier.buildClassifier(dataSet);

			// Evaluation eval = new Evaluation(dataSet);
			// eval.crossValidateModel(myRepTree, dataSet, 10, new Random(1));
			// System.out.println(eval.toSummaryString("\nResults\n======\n",
			// false));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void updateClassifier(int day, int hour, int minute, int requestNum) {
		Instance newInstance = (Instance) dataSet.firstInstance().copy();
		newInstance.setValue(0, day);
		newInstance.setValue(1, hour);
		newInstance.setValue(2, minute);
		newInstance.setClassValue(requestNum);
		dataSet.add(newInstance);
	}

	@Override
	public int predictRequestNum(int day, int hour, int minute) {
		Instance newInst = (Instance) dataSet.firstInstance().copy();
		newInst.setDataset(dataSet);
		// Attribute attr = new Attribute("tracedate", "dd:HH:mm:ss");

		newInst.setValue(0, day);
		newInst.setValue(1, hour);
		newInst.setValue(2, minute);

		int result = 0;
		try {
			// System.out.print(myRepTree.classifyInstance(dataSet.firstInstance()));
			result = (int) myClassifier.classifyInstance(newInst);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}

	public static void main(String[] args) {
		REPTreePredictor testPredictor = new REPTreePredictor();
		testPredictor.initClassifier("data/NASA_access_log_Aug95.arff");
		System.out.println(testPredictor.predictRequestNum(23, 15, 21));
	}
}
