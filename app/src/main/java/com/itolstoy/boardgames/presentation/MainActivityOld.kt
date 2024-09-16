package com.itolstoy.boardgames.presentation

//@AndroidEntryPoint
//class MainActivity : BaseAppCompatActivity<MainActivityViewState, MainActivityViewModel>() {
//
//    //private val viewModel: MainActivityViewModel by viewModels()
//
//    override fun createViewModel(): MainActivityViewModel {
//        val vm: MainActivityViewModel by viewModels()
//        return vm
//    }
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//
//        initViewStateLiveData()
//
//        setContent {
//            BoardGamesTheme {
//                // A surface container using the 'background' color from the theme
//                Surface(color = MaterialTheme.colors.background) {
//                    //Greeting("Android")
//                    GamesScreen(navController = rememberNavController())
//                }
//            }
//        }
//
//        //viewModel.createGamer("gamer1", "imageUrl1")
//        //viewModel.createSession("06.04.2022", mapOf(111 to 1, 222 to 2, 333 to 3), "gameId1")
//        //viewModel.createGame("gameName", "gameImageUrl", "description")
//    }
//
//    override fun viewStateHandler(viewState: MainActivityViewState): () -> Unit = when(viewState) {
//        MainActivityViewState.OK -> {
//            {}
//        }
//        MainActivityViewState.Loading -> {
//            {}
//        }
//        is MainActivityViewState.Error -> {
//            {
//                Toast.makeText(this, viewState.message, Toast.LENGTH_LONG).show()
//            }
//        }
//        MainActivityViewState.GamerCreated -> {
//            {
//                Toast.makeText(this, "Gamer was created", Toast.LENGTH_LONG).show()
//            }
//        }
//        MainActivityViewState.SessionCreated -> {
//            {
//                Toast.makeText(this, "Session was created", Toast.LENGTH_LONG).show()
//            }
//        }
//        MainActivityViewState.GameCreated -> {
//            {
//                Toast.makeText(this, "Game was created", Toast.LENGTH_LONG).show()
//            }
//        }
//    }
//}
//
//
//@Composable
//fun Greeting(name: String) {
//    Text(text = "Hello $name!")
//}
//
//@Preview(showBackground = true)
//@Composable
//fun DefaultPreview() {
//    BoardGamesTheme {
//        Greeting("Android")
//    }
//}