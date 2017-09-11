package com.arjun.impala;

import java.io.File;
import java.io.FileNotFoundException;

/**
 * 
 * Impala Query profile parser client. Accept profile path as argument.
 * 
 * @author arajan
 *
 */
public class ProfileParserClient {

	public static void main(String[] args) throws Exception {

		if (args.length == 0) {

			System.out.println("Usage:ProfileParserClient <profile input file> <optional output file>");
			System.exit(1);
		}

		ProfileParser parser = new ProfileParser();

		String inputFile = args[0];

		File profileFile = new File(inputFile);

		if (!profileFile.exists()) {

			throw new FileNotFoundException(String.format("File %s not found.Existing.", inputFile));
		}

		parser.parse(profileFile);

		if (parser.isSuccess()) {

			String outputFile = args.length == 2 ? args[1] : null;

			if (outputFile != null && !outputFile.isEmpty()) {

				parser.writeToFile(outputFile);
			} else {

				parser.printParsedProfiles();
			}
		} else {

			System.err.println("Parsing failed!.");

		}
	}

}