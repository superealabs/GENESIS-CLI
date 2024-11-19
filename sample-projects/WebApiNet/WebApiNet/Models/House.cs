namespace WebApiNet.Models;

using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;

[Table("houses")]
public class House  
{
	[Key]
	[DatabaseGenerated(DatabaseGeneratedOption.Identity)]
	[Column("house_id")]
	public long HouseId { get; set; }

	[Column("address")]
	public string Address { get; set; }

	[Column("city")]
	public string City { get; set; }

	[Column("state")]
	public string State { get; set; }

	[Column("zip_code")]
	public string ZipCode { get; set; }

	[Column("num_bedrooms")]
	public int NumBedrooms { get; set; }

	[Column("num_bathrooms")]
	public int NumBathrooms { get; set; }

	[Column("square_footage")]
	public int SquareFootage { get; set; }

	[Column("price")]
	public double Price { get; set; }

	[Column("year_built")]
	public int YearBuilt { get; set; }
	

}
