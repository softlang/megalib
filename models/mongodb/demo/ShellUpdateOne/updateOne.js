db.employees.updateOne(
	{ name: "Erik" },
	{ $set: { salary: 123456 } }
);
