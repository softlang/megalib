var Companies = require("./Structure");

var TransparentCompany = Class("TransparentCompany", {
    isa: Companies.Company,

    before: {
        cut: function() {
            console.log("We are deeply sorry to cut your salaries...");
        }
    },
    after: {
        cut: function() {
            console.log("Cut complete...please report...\n");
            this.report();
        }
    }
});

var emp1 = new Companies.Employee({ name: "Dmitri", address: "Frankfurt", salary: 3000 });
var emp2 = new Companies.Employee({ name: "Jannic", address: "Koblenz", salary: 3500 });
var freelancer = new Companies.Freelancer({ name: "Robert", address: "Koblenz", wage: 60, hours: 160 });

var company = new TransparentCompany({ name: "VIT", employees: [emp1, emp2, freelancer] });
company.report();
company.cut();