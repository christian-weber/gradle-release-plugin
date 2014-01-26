## Preconditions
 
Because the gradle-release-plugin works with SVN the following folder structures must be given.
 
  - trunk
  - branches
  - tags
 
## Custom GRADLE tasks
 
The *gradle-release-plugin* defines three custom tasks.

<table border="0">
	<tr>
		<th>createTag</th>
		<td>creates a Tag of the current project</th>
	</tr>
	<tr>
		<th>createBranch</th>
		<td>creates a Branch of the current project</th>
	</tr>
	<tr>
		<th>setVersion</th>
		<td>sets the version of the current project</th>
	</tr>
</table> 

## Task: createTag
 
     gradle createTag -Pusername=svnuser -Ppassword=svnpw -Purl=http://location/trunk 
     -PdevelopmentVersion=1.1-SNAPSHOT -PtagVersion=1.0
 
 <table border="0">
	<tr>
		<th>Name</th>
		<th>Description</th>
	</tr>
	<tr>
		<td>username</th>
		<td>The SVN account username used to connect to the SVN server</th>
	</tr>
	<tr>
		<td>password</th>
		<td>The SVN account password used to connect to the SVN server</th>
	</tr>
	<tr>
		<td>url</th>
		<td>The SVN URL to connect with</th>
	</tr>
	<tr>
		<td>developmentVersion</th>
		<td>The new project version of the branch</th>
	</tr>
</table> 
 
 The task requires the arguments username, password, url and developmentVersion.
 If one of them is missing an error message is displayed and the task execution stops.
 
## Task: createBranch
 
    gradle createBranch -Pusername=svnuser -Ppassword=svnpw -Purl=http://location/trunk 
    -PdevelopmentVersion=1.1-SNAPSHOT -PtagVersion=1.0
 
<table border="0">
	<tr>
		<th>Name</th>
		<th>Description</th>
	</tr>
	<tr>
		<td>username</th>
		<td>The SVN account username used to connect to the SVN server</th>
	</tr>
	<tr>
		<td>password</th>
		<td>The SVN account password used to connect to the SVN server</th>
	</tr>
	<tr>
		<td>url</th>
		<td>The SVN URL to connect with</th>
	</tr>
	<tr>
		<td>developmentVersion</th>
		<td>The new project version of the branch</th>
	</tr>
	<tr>
		<td>tagVersion</th>
		<td>The new project version of the tag</th>
	</tr>
</table> 
 
 The task requires the arguments username, password, url, developmentVersion and tagVersion.
 If one of them is missing an error message is displayed and the task execution stops.
 
 
## Task: setVersion
 
      gradle setVersion -Pusername=svnuser -Ppassword=svnpw -Purl=http://location/trunk 
      -PdevelopmentVersion=1.1-SNAPSHOT

<table border="0">
	<tr>
		<th>Name</th>
		<th>Description</th>
	</tr>
	<tr>
		<td>username</th>
		<td>The SVN account username used to connect to the SVN server</th>
	</tr>
	<tr>
		<td>password</th>
		<td>The SVN account password used to connect to the SVN server</th>
	</tr>
	<tr>
		<td>url</th>
		<td>The SVN URL to connect with</th>
	</tr>
	<tr>
		<td>developmentVersion</th>
		<td>The new project version of the branch</th>
	</tr>
</table> 
 
 The task requires the arguments username, password, url and developmentVersion.
 If one of them is missing an error message is displayed and the task execution stops.
 
 
