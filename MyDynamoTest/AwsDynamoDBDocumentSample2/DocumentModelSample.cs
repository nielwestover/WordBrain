/*******************************************************************************
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
using System.Collections.Generic;

using Amazon.DynamoDBv2;
using Amazon.DynamoDBv2.Model.Internal;
using Amazon.DynamoDBv2.DocumentModel;
using Newtonsoft.Json;
using System.IO;


class Pack
{
    public string pack;
    public List<Answers> levels;
}

class Answers 
{
    public List<string> answers;
}


namespace AwsDynamoDBDocumentSample1
{
    public partial class Program
    {
        public static void RunDocumentModelSample(AmazonDynamoDBClient client)
        {
            Console.WriteLine("Loading hints table");
            Table table = Table.LoadTable(client, "packLevels");

            Console.WriteLine("Creating and saving first item");
           
            string wordbrain = @"D:\wordbrain.json";
            List<Pack> packs = JsonConvert.DeserializeObject<List<Pack>>(File.ReadAllText(wordbrain)) as List<Pack>;
            int packNum = 0;
            foreach (Pack p in packs)
            {
                packNum++;
                int level = 0;
                foreach (Answers item in p.levels)
                {
                    Document mydoc = new Document();
                    mydoc["puzzleID"] = p.pack + "_" + level;
                    DynamoDBList list = new DynamoDBList();
                    foreach (string str in item.answers)
                        list.Add(str);
                    mydoc.Add("answers", list);
                    table.PutItem(mydoc);
                    ++level;
                }
                
            }
        }
    }
}
