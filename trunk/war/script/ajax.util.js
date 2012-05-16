var HOME = 'home';
var ENTITY_INBOX = 'inbox';
var ENTITY_SENT = 'sent';

// function to initialize the page
var init = function() {
	// showing the home tab on initializing
	showTab(HOME);
	// adding event listeners to the tabs
	$('#tabs a').click(function(event) {
		showTab(event.currentTarget.id);
	});
	// document.getElementById("loginBtn").onclick = loginUser;
}

// function to show the tab
var showTab = function(entity) {
	// remove the active class from all the tabs
	$('.tab').removeClass("active");
	// setting the active class to the selected tab
	$('#' + entity).addClass("active");
	// hiding all the tabs
	$('.g-unit').hide();
	// showing the selected tab
	$('#' + entity + '-tab').show();
	// hiding the message block
	$('.message').hide();
	// hiding the create block
	showHideCreate(entity, false);
	if (entity != HOME)
		$('#' + entity + '-search-reset').click();
}

// function to show/hide create block for an entity in a tab
var showHideCreate = function(entity, show) {
	// checking if the block is show or not
	if (show) {
		// hiding the search container
		$('#' + entity + '-search-ctr').hide();
		// hiding the list container
		$('#' + entity + '-list-ctr').hide();
		// showing the create container
		$('#' + entity + '-create-ctr').show();

	} else {
		// showing the search container
		$('#' + entity + '-search-ctr').show();
		// showing the list container
		$('#' + entity + '-list-ctr').show();
		// hiding the create container
		$('#' + entity + '-create-ctr').hide();
		// checking if the entity is not a home then populating the list of the
		// entity
		if (entity != HOME)
			populateList(entity, null);
	}
}

// parameter object definition
var param = function(name, value) {
	this.name = name;
	this.value = value;
}

// function to add an entity when user clicks on the add button in UI
var add = function(entity) {
	$('#' + entity + '-reset').click();
	// display the create container
	showHideCreate(entity, true);
	$("span.readonly input").attr('readonly', false);
}

// function to search an entity when user inputs the value in the search box
var search = function(entity) {
	$('.message').hide();
	// collecting the field values from the form
	var formEleList = $('form#' + entity + '-search-form').serializeArray();
	// assigning the filter criteria
	var filterParam = new Array();
	for ( var i = 0; i < formEleList.length; i++) {
		filterParam[filterParam.length] = new param(formEleList[i].name,
				formEleList[i].value);
	}
	// calling population of the list through ajax
	populateList(entity, filterParam);
}

var showMessage = function(message, entity) {
	$('#' + entity + '-show-message').show().html(
			'<p><b>' + message + '</b></p>');
}

var formValidate = function(entity) {
	var key;
	var formEleList = $('form#' + entity + '-create-form').serializeArray();
	key = formEleList[0].value;
	switch (entity) {
	case ENTITY_INBOX:
		// Recipient
		var hasError = false;
		// var emailReg = /^([\w-\.]+@([\w-]+\.)+[\w-]{2,4})?$/;
		var emailaddressVal = $("#recipient").val();
		if (emailaddressVal == '' || key == "") {
			hasError = true;
		}
		if (hasError == true) {
			showMessage('please check the recipient email value in the form',
					entity);
			return;
		}
		// Subject
		var valueSubject = $('#subject').val();
		if (valueSubject == "" || key == "") {
			showMessage('please check the subject in the form', entity);
			return;
		}
		// Body
		var valueSubject = $('#body').val();
		if (valueSubject == "" || key == "") {
			showMessage('please check the body in the form', entity);
			return;
		}
		break;
	case ENTITY_SENT:
		var valueCustomer = $('#order-customer-list').val();
		var valueItem = $('#order-item-list').val();
		if (valueCustomer == "" || valueItem == "") {
			showMessage(
					'please check the Customer and Item values in the form',
					entity);
			return;
		}
		break;
	default:
		if (key == "") {
			showMessage('please check the values in the form', entity);
			return;
		}
		break;
	}
	save(entity);
	$('.message').hide();
}

// function to save an entity
var save = function(entity) {
	$('.message').hide();
	// creating the data object to be sent to backend
	var data = new Array();
	// collecting the field values from the form
	var formEleList = $('form#' + entity + '-create-form').serializeArray();
	for ( var i = 0; i < formEleList.length; i++) {
		data[data.length] = new param(formEleList[i].name, formEleList[i].value);
	}
	// setting action as PUT
	data[data.length] = new param('action', 'PUT');
	// making the ajax call
	$.ajax({
		url : "/" + entity,
		type : "POST",
		data : data,
		success : function(data) {
			$('#' + entity + '-show-message').hide();
			showHideCreate(entity, false);
		}
	});
	$('#' + entity + '-reset').click();
}

// function called when user clicks on the cancel button
var cancel = function(entity) {
	$('#' + entity + '-show-message').hide();
	// hiding the create container in the tab
	showHideCreate(entity, false);
}

// function to delete an entity
var deleteEntity = function(entity, id, parentid) {
	var parameter = new Array();
	parameter[parameter.length] = new param('id', id);
	parameter[parameter.length] = new param('parentid', parentid);
	parameter[parameter.length] = new param('action', 'DELETE');
	// making the ajax call
	$.ajax({
		// url : "/" + entity,
		url : "/inbox",
		type : "POST",
		data : parameter,
		dataType : "html",
		success : function(resp) {
			showHideCreate(entity, false);
			if (resp != '') {
				showMessage(resp, entity);
			}

		},
		error : function(resp) {
			showMessage(resp, entity);
		}
	});
}

// function to get the data by setting url, filter, success function and error
// function
var getData = function(url, filterData, successFn, errorFn) {
	// making the ajax call
	$.ajax({
		url : url,
		type : "GET",
		data : filterData,
		success : function(resp) {
			// calling the user defined success function
			if (successFn)
				successFn(resp);
		},
		error : function(e) {
			// calling the user defined error function
			if (errorFn)
				errorFn(e);
		}
	});
}

// function to populate the list of an entity
var populateList = function(entity, filter) {
	// specifying the success function. When the ajax response is successful
	// then the following function will be called

	var successFn = function(resp) {
		var data = '';
		if (resp) {
			// getting the data from the response object
			data = resp.data;
		}
		// creating the html content
		var htm = '';
		if (data.length > 0) {
			for ( var i = 0; i < data.length; i++) {
				// creating a row
				htm += '<tr>';
				switch (entity) {
				case ENTITY_INBOX:
					htm += '<td>' + data[i].recipient + '</td><td>'
							+ data[i].subject + '</td><td>' + data[i].body
							+ '</td><td>' + data[i].receivedOn + '</td>';
					break;
				case ENTITY_SENT:
					htm += '<td>' + data[i].recipient + '</td><td>'
							+ data[i].subject + '</td><td>' + data[i].body
							+ '</td><td>' + data[i].receivedOn + '</td>';
					break;
				default:
					htm += "";
				}
				htm += '<td><a href="#" class="delete-entity" onclick=\'deleteEntity("'
						+ entity
						+ '","'
						+ data[i].name
						+ '",null)\'>Delete</a></td></tr>';
			}
		} else {
			// condition to show message when data is not available
			var thElesLength = $('#' + entity + '-list-ctr table thead th').length;
			htm += '<tr><td colspan="' + thElesLength
					+ '">No records found</td></tr>';
		}
		$('#' + entity + '-list-tbody').html(htm);
	}
	getData("/" + entity, filter, successFn, null);
}

var login = function() {
	console.log('username=' + $('#username').val());
	var parameter = new Array();
	parameter[parameter.length] = new param('action', 'login');
	parameter[parameter.length] = new param('username', $('#username').val());
	// making the ajax call
	$.ajax({
		url : '/home',
		type : "GET",
		data : parameter,
		success : function(resp) {
			console.log('login success');
			$('#homeMessage').html(resp.data);
			$('#loginPage').hide();
			$('#logoutPage').show();
		},
		error : function(e) {
			// calling the user defined error function
		}
	});
}

var logout = function() {
	var parameter = new Array();
	parameter[parameter.length] = new param('action', 'logout');
	parameter[parameter.length] = new param('username', $('#username').val());
	// making the ajax call
	$.ajax({
		url : '/home',
		type : "GET",
		data : parameter,
		success : function(resp) {
			console.log('logout success');
			$('#homeMessage').html(resp.data);
			$('#loginPage').show();
			$('#logoutPage').hide();
		},
		error : function(e) {
			// calling the user defined error function
		}
	});
}
