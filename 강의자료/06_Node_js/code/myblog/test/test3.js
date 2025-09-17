const { MongoClient } = require("mongodb");
// Replace the uri string with your connection string
const uri = "mongodb://localhost:27017/mydb";
const client = new MongoClient(uri);

async function run() {
  try {
    await client.connect();
    const database = client.db('mydb');
    const movies = database.collection('movies');
    const result = await movies.insertOne({ title: 'Back to the Future', year: 1985, rating: 8.5 });
    
    // Queries for a movie that has a title value of 'Back to the Future'
    const query = { title: 'Back to the Future' };
    const movie = await movies.findOne(query);
    console.log('movie = ' + movie.title);
    //console.log("Connected successfully to server");
    
  } finally {
    await client.close();
  }
}

run().catch(console.dir);