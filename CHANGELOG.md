#Alpha1

This version covers only work about collections, not widgets. 

##Architecture

We have implemented our architecture in 3 layers: **app**, **process** and **services**. The process layer can access to all services but it don't access to other processes, and the services make easy works

The services are: ApiService, PersistenceService, AppsService, ContactService, WidgetService and ImageService

All services have been implemented least WidgetService. For now, we have investigated about how works with Widgets on Android and we have created a simple example about that and we'll use this job to create a new WidgetService soon

The processes are: UserProcess, UserConfig, CollectionsProcess, GooglePlayProcess, DevicesProcess and ThemeProcess

All processes have been implemented least GooglePlayProcess. We have implemented the API for calls to Google Play and it's possible future changes in the new server

##Feature added in this version

* **Wizard**: We have implemented a first approach of wizard. Every step in the wizard will have animations, but we don't have implemented the animations yet
* **Workspaces**: The workspaces are the spaces in the launcher where you can move between collections and moments. We load all collections in the workspaces and you can select a collection. The menu of collection have been implemented too, but not the actions for now
* **Search Bar**: You can open the App menu, open the Google Search and Google Action Voice
* **App Drawer Panel**: You can open the *App Drawer* from the center button. For now, we add 4 default application in App Drawer, the apps are: Hangout, Inbox, Chrome, and Camera
* **App Menu**: We can change the wallpaper, go to settings of Android and move between collections and moments. The avatar and email appear in the header of menu
* **App Drawer**: You can see the applications and contacts and you can use the FastScroller from the right edge for move quickly for the list. You can go to App Store and Contact App from FAB
* **Collections Details**: You can move between collections. The behaviours are: 
	* The color of collections is a really important thing. The toolbar changes when you move to other collection and all menus (FAB and items) and dialogs have the collection's color
	* When the collection has more of 9 cards, the toolbar is reduced with a new animation if you swipe in the list of cards
	* We have created a new Pull to Close. When you are in the top of cards, you can pull the list, when the icon in toolbar changes to close icon, you can drop the list and back to the workspaces
	* You can click in tabs and go to other collection directly 
	* You can remove cards doing a long press in the card. This way is only for this Alpha, we are going to change this behavior, we are working in a new design to edit collections
	* When you are moving for the list, appear a FAB to add new cards to the collection. We are going to talk about every kind of card in the following points
* **Add App to Collection**: When you open the dialog to add app to the collection, will appear all apps of the kind of collection that you are seeing. For example, if you are in Games, you only see applications about games. You can change to all apps from the switch button in the toolbar. If the collection isn't a collection categorized, will appear all apps and the switch button is removed. You can add applications clicking in an app in the list
* **Add Contacts to Collection**: When you open the dialog to add contacts to the collection, will appear all contacts with phone number. You can change to all contacts from the switch button in the toolbar.You can add contacts clicking in a contact in the list
* * **Add Shortcuts to Collection**: When you open the dialog to add shortcuts to the collection, will appear all shortcuts. You can add shortcut clicking in a shortcut in the list

##Unimplemented features

* **Wizard**: Animations in every step
* **Workspaces**: For now we don't have implemented: moments (widgets and current collection) and the actions of menu (create collection, my collections and public collections)
* **App Drawer Panel**: The user should be able to change the applications in App Drawer Panel
* **App Menu**: We have to create a User Profile and new 9Cards Settings
* **App Drawer**: The filters don't work for now
* **Collections Details**: We can't edit or share collections yet and we can't add recommendations to collection

##Known issues

We have a problem removing a card. If in the last row you have only 1 card, the list creates a padding on the top and the toolbar doesn't work fine. This implementation to remove cards in only this alpha. We are going to create another UI/UX implementation and this bug won't exist
