For this application I have used Clean Architecture which was created by Robert Martin to 
be used for a software. Here are the dependencies I have used for this project.

-Retrofit for API Calls.
-Coroutines and Kotlin Flow API for Asynchronous tasks.
-Dagger Hilt for Dependency Injection.
-Circleimageview for showing circular imageview.
-Glide to load network images.
-LiveData-Viewmodel for Viewmodel class.
-Junit and Mockserver for Unit Testing.

Clean Architecture has three layers - data,domain and presentation.
Data layer gathers data from outside resources like DB or Network.
Domain layer contains use cases which gathers data from data layer.
Presentation layers contains UI and Viewmodel (which holds state).

Phase 1 Approach -
I have used A RecyclerView which call the REST API and shows the list to the user.
Each item of RecyclerView shows Repository Name, Forks count and current active issues.
Both landscape and vertical orientation is supported. There are two XML files for each 
orientation. There is also a 'Open In Github' button which opens the repository in 
web browser. For selection of item, there is a flag for each item of the list called
'isSelected' which will turn to 'true' if tapped by user, and therefore color will change
to green. As this data exists as MutableStateFlow in ViewModel, it will survive orientation
change, and also position and selection will remain same. Lastly, I have used Kotlin Channel
to communicate between Viewmodel and UI, incase any API error happens.

Phase 2 Approach -
There is a MutableStateFlow called 'SearchQuery' in the viewmodel. I have used flatMap 
operator to monitor changes to 'SearchQuery'. Therefore, whenever a user types into 
SearchView, filtering will happen and filtered list will be emitted to the UI.
The selection will remain as it is even after search as all data is present in viewmodel
as MutableStateFlow. Once search is cleared, the original list is again emitted to the UI.

Unit Testing task - 
I have wrote basic code to test the API layer with JUnit4 and MockServer for class MainRepositoryImpl,
but it's not complete as I don't have full expertise in Unit Testing.