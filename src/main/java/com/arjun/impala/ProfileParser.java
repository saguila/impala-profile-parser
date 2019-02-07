package com.arjun.impala;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.zip.InflaterInputStream;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.apache.thrift.TDeserializer;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TCompactProtocol;

import com.cloudera.impala.thrift.TCounter;
import com.cloudera.impala.thrift.TEventSequence;
import com.cloudera.impala.thrift.TRuntimeProfileNode;
import com.cloudera.impala.thrift.TRuntimeProfileTree;

public class ProfileParser {

	private final static Logger log = Logger.getLogger(ProfileParser.class.getName());
	private List<String> parsedProfiles = new ArrayList<String>();

	/**
	 * Query profile is zlib compressed TRuntimeProfileTree object.This method
	 * uncompress and decode thrift encoded profile file.
	 * 
	 * @param profileFile
	 * @throws FileNotFoundException
	 */
	public void parse(File profileFile) throws FileNotFoundException {

		FileInputStream fis = new FileInputStream(profileFile);

		BufferedReader br = new BufferedReader(new InputStreamReader(fis));

		String line = null;
		int profileCnt = 0;
		try {
			// Single file may have multiple query profiles separated by
			// newline.
			while ((line = br.readLine()) != null) {

				log.info("Processing profile " + (++profileCnt));

				// query profile string : <epoch_timestamp> <hash of base64
				// encoded string> <base64 encoded string>

				byte[] data = line.split(" ")[2].getBytes();

				// Decoding base64 encoded query profile.
				byte[] decodedBytes = Base64.decodeBase64(data);

				InflaterInputStream in = new InflaterInputStream(new ByteArrayInputStream(decodedBytes));
				ByteArrayOutputStream out = new ByteArrayOutputStream();
				IOUtils.copy(in, out);

				// query profile is ZLib encoded TRuntimeProfileTree
				// object.Deserializing using TCompactProtocol.
				TRuntimeProfileTree profileTree = new TRuntimeProfileTree();
				TDeserializer deserializer = new TDeserializer(new TCompactProtocol.Factory());
				deserializer.deserialize(profileTree, out.toByteArray());

				// parsing deserialized TRuntimeProfileTree object.
				parseQueryProfile(profileTree);

			}
		} catch (IOException e) {
			e.printStackTrace();
			System.err.format("IOException: %s%n", e);

		} catch (TException e) {
			e.printStackTrace();
			System.err.format("Thrift Decoding Exception: %s%n", e);
		} finally {

			try {

				if (br != null)
					br.close();

			} catch (IOException e) {

				e.printStackTrace();
				System.err.format("IOException: %s%n", e);

			}
		}
	}

	/**
	 * Pasrse TRuntimeProfileTree object and print profile information.
	 * 
	 * @param profileTree
	 */
	private void parseQueryProfile(TRuntimeProfileTree profileTree) {

		// TRuntimeProfileTree object has list of profile node.
		List<TRuntimeProfileNode> profileNodeList = profileTree.getNodes();

		if (profileNodeList.isEmpty()) {
			log.info("Empty profile information!");
			return;
		}

		StringBuilder builder = new StringBuilder();
		// Setting query metadata including information like query id,query
		// string, start and end time etc..
		ProfileMetadata profileMetadata = new ProfileMetadata();

		profileMetadata.setQueryId(profileNodeList.get(0).getName().split("=")[1].replaceAll("\\)", ""));

		Map<String, String> summaryInfoMap = profileNodeList.get(1).getInfo_strings();
		profileMetadata.setUser(summaryInfoMap.get("User"));
		profileMetadata.setQuerystartTime(summaryInfoMap.get("Start Time"));
		profileMetadata.setQueryEndTime(summaryInfoMap.get("End Time"));
		profileMetadata.setQueryType(summaryInfoMap.get("Query Type"));
		profileMetadata.setQueryStatus(summaryInfoMap.get("Query State"));
		profileMetadata.setQueryString(summaryInfoMap.get("Sql Statement"));
		profileMetadata.setCoordinator(summaryInfoMap.get("Coordinator"));
		profileMetadata.setDefaultDb(summaryInfoMap.get("Default Db"));
		profileMetadata.setQueryPlan(summaryInfoMap.get("Plan"));
		profileMetadata.setExecSummary(summaryInfoMap.get("ExecSummary"));

		builder.append(profileMetadata).append("\n");

		// Iterate each node and print counter ,event and info strings.
		if (profileNodeList.size() > 2) {

			for (int i = 2; i < profileNodeList.size(); i++) {

				TRuntimeProfileNode profileNode = profileNodeList.get(i);

				builder.append("\n").append(profileNode.getName()+" : ");

				appendNodeCounterInfo(profileNode, builder);

				appendNodeEventInfo(profileNode, builder);

				appendNodeMapInfo(profileNode, builder);

			}
		}

		parsedProfiles.add(builder.toString());
	}

	/**
	 * Print TRuntimeProfileNode node counter information.
	 * 
	 * @param profileNode
	 */
	private void appendNodeCounterInfo(TRuntimeProfileNode profileNode, StringBuilder builder) {

		List<TCounter> nodeCounters = profileNode.getCounters();

		for (TCounter counter : nodeCounters) {

			builder.append("\n\t\t")
					.append(counter.getName() + ":" + counter.getValue() + " " + counter.getUnit().name());

		}
	}

	/**
	 * Print node event sequence information.
	 * 
	 * @param profileNode
	 */
	private void appendNodeEventInfo(TRuntimeProfileNode profileNode, StringBuilder builder) {
		List<TEventSequence> eventSequences = profileNode.getEvent_sequences();

		if (eventSequences != null && eventSequences.size() > 0) {

			for (TEventSequence eventSequence : eventSequences) {

				builder.append("\n").append(eventSequence.getName());

				for (int j = 0; j < eventSequence.getLabelsSize(); j++) {

					builder.append("\n\t\t")
							.append(eventSequence.getLabels().get(j) + ":" + eventSequence.getTimestamps().get(j));

				}

			}
		}
	}

	/**
	 * Print Node info string information.
	 * 
	 * @param profileNode
	 */
	private void appendNodeMapInfo(TRuntimeProfileNode profileNode, StringBuilder builder) {
		Map<String, String> nodeInfoStringMap = profileNode.getInfo_strings();

		for (Map.Entry<String, String> entry : nodeInfoStringMap.entrySet()) {
			builder.append("\n\t\t" + entry.getKey() + " : " + entry.getValue());
		}
	}

	public void writeToFile(String outputFile) {

		BufferedWriter bufferedWriter = null;
		FileWriter fileWriter = null;

		try {

			fileWriter = new FileWriter(outputFile,false);
			bufferedWriter = new BufferedWriter(fileWriter);

			int i = 0;
			for (String parsedProfile : parsedProfiles) {

				bufferedWriter.write(("\n\nProfile:" + (++i)) + "\n\n");

				bufferedWriter.write(parsedProfile);
			}

		} catch (IOException e) {

			e.printStackTrace();

		} finally {

			try {

				if (bufferedWriter != null)
					bufferedWriter.close();

				if (fileWriter != null)
					fileWriter.close();

			} catch (IOException ex) {

				ex.printStackTrace();

			}

		}

	}

	public void printParsedProfiles() {

		int i = 0;
		for (String parsedProfile : parsedProfiles) {
			System.out.println("\n\n*******************************************************************");
			System.out.println("\nProfile:" + (++i));
			System.out.println(parsedProfile);
		}

	}

	public boolean isSuccess() {

		return (parsedProfiles.isEmpty() ? false : true);

	}
}