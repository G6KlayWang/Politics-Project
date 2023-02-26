import java.util.Iterator;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.neural.rnn.RNNCoreAnnotations;
import edu.stanford.nlp.sentiment.SentimentCoreAnnotations;
import edu.stanford.nlp.sentiment.SentimentCoreAnnotations.SentimentAnnotatedTree;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.util.CoreMap;
import edu.stanford.nlp.ling.CoreAnnotations;
import java.util.List;
import java.util.Properties;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;

public class nlpPipeline{
	static StanfordCoreNLP pipeline;
	public static void init() 
	{
	    Properties props = new Properties();
	    props.setProperty("annotators", "tokenize, ssplit, parse, sentiment");
	    pipeline = new StanfordCoreNLP(props);
	}
	public static int estimatingSentiment(String text){
		int sentimentInt = 0;
		String sentimentName; 
		Annotation annotation = pipeline.process(text);
		  for(CoreMap sentence : annotation.get(CoreAnnotations.SentencesAnnotation.class))
		  {
		     Tree tree = sentence.get(SentimentAnnotatedTree.class);
		    sentimentInt = RNNCoreAnnotations.getPredictedClass(tree); 
		    sentimentName = sentence.get(SentimentCoreAnnotations.SentimentClass.class);
		    System.out.println(sentimentName + "\t" + sentimentInt + "\t" + sentence);
		  }
		  return sentimentInt;
	}
}
