var Companies = require("./Structure");

var emp1 = new Companies.Employee({ name: "Dmitri", address: "Frankfurt", salary: 3000 });
var emp2 = new Companies.Employee({ name: "Jannic", address: "Koblenz", salary: 3500 });
var freelancer = new Companies.Freelancer({ name: "Robert", address: "Koblenz", wage: 60, hours: 160 });

var company = new Companies.Company({ name: "VIT", employees: [emp1, emp2, freelancer] });
company.report();

//console.log(company.getName)       // method
//console.log(company.setName)       // undefined
//console.log(company.getEmployees)    // method
//console.log(company.setEmployees)    // method

