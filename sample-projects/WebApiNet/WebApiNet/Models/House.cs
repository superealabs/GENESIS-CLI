namespace WebApiNet.Models;

using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;

[Table("Houses")]
public class House  
{
	[Key]
	[DatabaseGenerated(DatabaseGeneratedOption.Identity)]
	[Column("HouseId")]
	public int Houseid { get; set; }

	[Column("Address")]
	public string Address { get; set; }

	[Column("City")]
	public string City { get; set; }

	[Column("State")]
	public string State { get; set; }

	[Column("ZipCode")]
	public string Zipcode { get; set; }

	[Column("PurchasePrice")]
	public double Purchaseprice { get; set; }

	[Column("SellPrice")]
	public double Sellprice { get; set; }

	[Column("YearBuilt")]
	public int Yearbuilt { get; set; }

	[Column("BuildingType")]
	public string Buildingtype { get; set; }
	

}
