i = 0
File.open('baseoriginal.txt', 'r') do |f1|
  while linea = f1.gets
    File.open('rebotados.txt', 'r') do |f2|
      while linea2 = f2.gets
        if linea == linea2
          #puts "mail que existe en rebotados! - se debe ignorar" + linea
          sucio = 1
          break
        else
          sucio = 0
        end
      end #fin while 2
      unless sucio == 1 
        puts linea
        i = i + 1
      end
    end #fin segundo file.open
  end #fin primer while
end #fin primer open
puts "Total limpios " + i.to_s

