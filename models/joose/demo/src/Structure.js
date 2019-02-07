var Class = require("joose").Class;


var Company = Class("Company", {
    has: {
        name: { is: "r" },
        employees: { is: "rw", init: [] }
    },
    methods: {
        cut: function() {
            this.getEmployees().forEach(e => e.cut());
        },
        report: function() {
            console.log("Company", this.getName());
            this.getEmployees().forEach(e => console.log(e.report()));
            console.log("\n\n")
        }
    }
});

var Employee = Class("Employee", {
    has: {
        name: { is: "r" },
        address: { is: "rw" },
        salary: { is: "rw" }
    },
    methods: {
        cut: function() {
            this.setSalary(this.getSalary() / 2);
        },
        report: function() {
            console.log("I am employee", this.getName(), "living in", this.getAddress(), "and earning", this.getSalary())
        }
    }
});

var Freelancer = Class("Freelancer", {
    isa: Employee,
    has: {
        wage: { is: "rw" },
        hours: { is: "rw" }
    },
    methods: {
        cut: function() {
            this.setWage( this.getWage() / 2 );
        },

        report: function() {
            console.log("I am freelancer", this.getName(), "with a total salary of", this.getWage() * this.getHours(), "and living in", this.getAddress())
        }
    }
});

module.exports = {
    Company,
    Employee,
    Freelancer
};

