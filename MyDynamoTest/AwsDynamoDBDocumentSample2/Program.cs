﻿/*******************************************************************************
* Copyright 2009-2013 Amazon.com, Inc. or its affiliates. All Rights Reserved.
* 
* Licensed under the Apache License, Version 2.0 (the "License"). You may
* not use this file except in compliance with the License. A copy of the
* License is located at
* 
* http://aws.amazon.com/apache2.0/
* 
* or in the "license" file accompanying this file. This file is
* distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
* KIND, either express or implied. See the License for the specific
* language governing permissions and limitations under the License.
*******************************************************************************/

using System;

using Amazon;
using Amazon.DynamoDBv2;
using System.Collections.Generic;
using Newtonsoft.Json;
using System.IO;


namespace AwsDynamoDBDocumentSample1
{
    public partial class Program
    {
        public static void Main(string[] args)
        {            
            Console.WriteLine();
            Console.WriteLine("Setting up DynamoDB client");
            AmazonDynamoDBClient client = new AmazonDynamoDBClient();

            Console.WriteLine();
            Console.WriteLine("Creating sample tables");
            //TableOperations.CreateSampleTables(client);

            Console.WriteLine();
            Console.WriteLine("Running Document sample");
            RunDocumentModelSample(client);

            //Console.WriteLine();
            //Console.WriteLine("Removing sample tables");
            //TableOperations.DeleteSampleTables(client);
            Console.WriteLine();
            Console.WriteLine("Press Enter to continue...");
            Console.ReadLine();
        }
    }
}