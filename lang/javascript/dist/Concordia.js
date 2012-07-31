
function Concordia(schema){'use strict';(function(concordia){function validateSchemaType(obj){}
function validateDataType(schema,data){}
function validateSchemaBooleanType(obj){}
function validateDataBooleanType(schema,data){var dataType=typeof data;if((data===null)||(dataType==="undefined")){if(!schema.optional){throw"The data is null and not optional.";}}
else if(dataType!=="boolean"){throw"The value is not a boolean: "+
JSON.stringify(data,null,null);}}
function validateSchemaNumberType(obj){}
function validateDataNumberType(schema,data){var dataType=typeof data;if((data===null)||(dataType==="undefined")){if(!schema.optional){throw"The data is null and not optional.";}}
else if(dataType!=="number"){throw"The value is not a number: "+
JSON.stringify(data,null,null);}}
function validateSchemaStringType(obj){}
function validateDataStringType(schema,data){var dataType=typeof data;if((data===null)||(dataType==="undefined")){if(!schema.optional){throw"The data is null and not optional.";}}
else if(dataType!=="string"){throw"The data is not a string: "+
JSON.stringify(data,null,null);}}
function validateSchemaObjectType(obj){var schema=obj.schema,schemaType=typeof schema,i,field,fieldType,name,nameType;if(schema===null){throw"The 'schema' field's value cannot be null: "+
JSON.stringify(obj,null,null);}
if(schemaType==="undefined"){throw"The 'schema' field is missing: "+
JSON.stringify(obj,null,null);}
if((schemaType!=="object")||(!(schema instanceof Array))){throw"The 'schema' field's value must be a JSON array: "+
JSON.stringify(obj,null,null);}
for(i=0;i<schema.length;i+=1){field=schema[i];if(field===null){throw"The element at index "+
i+" of the 'schema' field is null: "+
JSON.stringify(obj,null,null);}
fieldType=typeof field;if((fieldType!=="object")||(field instanceof Array)){throw"The element at index "+
i+" of the 'schema' field is not a JSON object: "+
JSON.stringify(obj,null,null);}
name=field.name;if(name===null){throw"The 'name' field for the JSON object at index "+
i+" is null: "+
JSON.stringify(obj,null,null);}
nameType=typeof name;if(nameType==="undefined"){throw"The 'name' field for the JSON object at index "+
i+" is misisng: "+
JSON.stringify(obj,null,null);}
if(nameType!=="string"){throw"The type of the 'name' field for the JSON object at index "+
i+" is not a string: "+
JSON.stringify(obj,null,null);}
validateSchemaType(field);}}
function validateDataObjectType(schema,data){var dataType=typeof data,i,schemaFields,schemaField,name,dataField;if((data===null)||(dataType==="undefined")){if(!schema.optional){throw"The data is not optional.";}
else{return;}}
if((typeof data!=="object")||(data instanceof Array)){throw"The data is not a JSON object: "+
JSON.stringify(data,null,null);}
schemaFields=schema.schema;for(i=0;i<schemaFields.length;i+=1){schemaField=schemaFields[i];name=schemaField.name;dataField=data[name];if((typeof dataField==="undefined")&&(!(schemaField.optional))){throw"The field '"+
name+"' is missing from the data: "+
JSON.stringify(data,null,null);}
validateDataType(schemaField,dataField);}}
function validateSchemaArrayType(obj){var schema=obj.schema,schemaType;if(schema===null){throw"The 'schema' field's value cannot be null: "+
JSON.stringify(obj,null,null);}
schemaType=typeof schema;if(schemaType==="undefined"){throw"The 'schema' field is missing: "+
JSON.stringify(obj,null,null);}
if(schemaType!=="object"){throw"The 'schema' field's type must be either an array or an object: "+
JSON.stringify(obj,null,null);}
if(schema instanceof Array){validateSchemaConstLengthArray(schema);}
else{validateSchemaConstTypeArray(schema);}}
function validateDataArrayType(schema,data){var dataType=typeof data,arraySchema;if((data===null)||(dataType==="undefined")){if(!schema.optional){throw"The data is not optional.";}}
if((dataType!=="object")||(!(data instanceof Array))){throw"The data is not a JSON array: "+
JSON.stringify(data,null,null);}
arraySchema=schema.schema;if(arraySchema instanceof Array){validateDataConstLengthArray(arraySchema,data);}
else{validateDataConstTypeArray(arraySchema,data);}}
function validateSchemaConstLengthArray(arr){var i,field,fieldType;for(i=0;i<arr.length;i++){field=arr[i];if(field===null){throw"The element at index "+
i+" is null: "+
JSON.stringify(obj,null,null);}
fieldType=typeof field;if((fieldType!=="object")||(field instanceof Array)){throw"The element at index "+
i+"is not a JSON object: "+
JSON.stringify(obj,null,null);}
validateSchemaType(field);}}
function validateDataConstLengthArray(schema,dataArray){var i;if(schema.length!==dataArray.length){throw"The schema array and the data array are of different lengths: "+
JSON.stringify(dataArray,null,null);}
for(i=0;i<schema.length;i++){validateDataType(schema[i],dataArray[i]);}}
function validateSchemaConstTypeArray(obj){validateSchemaType(obj);}
function validateDataConstTypeArray(schema,dataArray){var i;for(i=0;i<dataArray.length;i++){validateDataType(schema,dataArray[i]);}}
function validateSchemaType(obj){var type=obj.type;if(type==="boolean"){validateSchemaBooleanType(obj);}
else if(type==="number"){validateSchemaNumberType(obj);}
else if(type==="string"){validateSchemaStringType(obj);}
else if(type==="object"){validateSchemaObjectType(obj);}
else if(type==="array"){validateSchemaArrayType(obj);}
else if(type===null){throw"The 'type' field cannot be null: "+
JSON.stringify(obj,null,null);}
else if(typeof type==="undefined"){throw"The 'type' field is missing: "+
JSON.stringify(obj,null,null);}
else{throw"Type unknown: "+type;}
validateSchemaTypeOptions(obj);}
function validateDataType(schema,data){var type=schema.type;if(type==="boolean"){validateDataBooleanType(schema,data);}
else if(type==="number"){validateDataNumberType(schema,data);}
else if(type==="string"){validateDataStringType(schema,data);}
else if(type==="object"){validateDataObjectType(schema,data);}
else if(type==="array"){validateDataArrayType(schema,data);}}
function validateSchemaTypeOptions(obj){var doc=obj.doc,docType=typeof doc,optional,optionalType;if((docType!=="undefined")&&(docType!=="string")){throw"The 'doc' field's value must be of type string: "+
JSON.stringify(obj,null,null);}
var optional=obj.optional;var optionalType=typeof optional;if((optionalType!=="undefined")&&(optionalType!=="boolean")){throw"The 'optional' field's value must be of type boolean: "+
JSON.stringify(obj,null,null);}}
function validateSchema(obj){var type=obj.type,typeType=typeof type,optionalType=typeof obj.optional;if(type===null){throw"The root object's 'type' field cannot be null: "+
JSON.stringify(obj,null,null);}
if(typeType==="undefined"){throw"The root object's 'type' field is missing: "+
JSON.stringify(obj,null,null);}
if((type!=="object")&&(type!=="array")){throw"The root object's 'type' field must either be "+"'object' or 'array': "+
JSON.stringify(obj,null,null);}
if(optionalType!=="undefined"){throw"The 'optional' field is not allowed at the root of the definition.";}
validateSchemaType(obj);return obj;}
concordia.validateData=function(data){var jsonData=data;if(typeof data==="string"){jsonData=JSON.parse(data);}
if(typeof jsonData==="object"){validateDataType(concordia.schema,jsonData);}
else{throw"The data must either be a JSON object or a JSON array or a string representing one of the two.";}
return jsonData;};var schemaJson=schema;var schemaType=typeof schema;if(schemaType==="string"){schemaJson=JSON.parse(schema);}
else if((schemaType!=="object")||(schema instanceof Array)){throw"The schema must either be a JSON object or a string representing a JSON object.";}
concordia.schema=validateSchema(schemaJson);}(this));}