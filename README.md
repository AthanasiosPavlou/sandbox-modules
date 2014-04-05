This project, is a GWT project and was created with Eclipse (4.3) Kepler as described in:


http://www.gwtproject.org/usingeclipse.html.

However, when downloading the files submitted here, Eclipse will not recognise the repository as an existing GWT project because the relevant .project file is not here.

Thus, once you get (clone) the repository on your machine, follow these steps:

Open "Git Repository Exploring" perspective

If the perspective is not available, the plugin needs to be downloaded:
Help > Install new software > add: http://download.eclipse.org/releases/kepler

Search for git

Check to install the following:
Eclipse Git Team Provider,
Eclipse GitHub integration with task focused interface,
Mylin Versions Connector:Git
Task focused interface for Eclipse Git Team Provider.

Right click on the repository and select "Import Projects" and select Import As General Project.

Then, open a perspective, such as Debug, and naviate to the imported project (in this case blipnipsandbox).

Right click on the project > Google > Web Toolkit Settings

Check the Use Google Web Toolkit (make sure that you are using SDK: GWT-2.5.1)

Similarly go to Google App Engine and check "Use Google App Engine" (make sure you use 1.8.4)

The project will show many errors now, due to not having included the required jars.

All the jars are found in the extlibs folder, thus everything needs to be imported from there.

Select Add Library

User Libraries

New > Enter user library name. Its best to keep the same name as in the extlibs folder.


Add them all one by one.
Clean the project.

All the jars should now be added and imports should be ok.

(for more details check: https://developers.google.com/eclipse/docs/existingprojects)

//new change
