namespace WebApiNet.Models;

using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;

[Table("testing")]
public class Testing  
{
	[Key]
	[DatabaseGenerated(DatabaseGeneratedOption.Identity)]
	[Column("id")]
	public long Id { get; set; }

	[Column("name")]
	public string Name { get; set; }

	[Column("email")]
	public string Email { get; set; }

	[Column("phone")]
	public string Phone { get; set; }
	

}
